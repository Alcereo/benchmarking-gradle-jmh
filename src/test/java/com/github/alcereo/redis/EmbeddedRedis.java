package com.github.alcereo.redis;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import org.junit.jupiter.api.extension.*;

import java.util.UUID;

public class EmbeddedRedis implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    private static String redisContainerId;
    private static DockerClient dockerCli;
    private static String redisPort;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .build();

        dockerCli = DockerClientBuilder.getInstance(config).build();

        ExposedPort tcp6379 = ExposedPort.tcp(6379);
        Ports portBinding = new Ports();
        portBinding.bind(tcp6379, Ports.Binding.empty());
        CreateContainerResponse redisContainer = dockerCli.createContainerCmd("redis:4.0.9-alpine")
                .withStdInOnce(true)
                .withName("redis-test-"+UUID.randomUUID())
                .withExposedPorts(tcp6379)
                .withPortBindings(portBinding)
                .exec();

        redisContainerId = redisContainer.getId();

        dockerCli.startContainerCmd(redisContainerId).exec();

        Ports.Binding[] bindings = dockerCli.inspectContainerCmd(redisContainerId).exec()
                .getNetworkSettings()
                .getPorts()
                .getBindings()
                .get(tcp6379);

        redisPort = bindings[0].getHostPortSpec();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        dockerCli.stopContainerCmd(redisContainerId).exec();
        dockerCli.removeContainerCmd(redisContainerId).exec();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.isAnnotated(RedisPort.class);
    }

    @Override
    public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return redisPort;
    }
}
