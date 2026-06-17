package com.landisgyr.gfc.flexhub_connector.app;

import com.landisgyr.gfc.flexhub_connector.infrastructure.build.GitInfoManager;
import com.landisgyr.gfc.flexhub_connector.infrastructure.di.Application;
import com.landisgyr.gfc.flexhub_connector.infrastructure.di.ApplicationComponent;
import com.landisgyr.gfc.flexhub_connector.infrastructure.di.DaggerApplicationComponent;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import io.grpc.Server;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static final String SERVICE_NAME = "flex-hub-connector";

  @Inject Server server;

  @Inject GitInfoManager gitInfoManager;

  public void run() throws IOException, InterruptedException {
    // Use like: -Dconfig.file=etc/application.conf
    Config config = loadConfig();
    ApplicationComponent appComponent = DaggerApplicationComponent.factory().create(config);
    Application app = appComponent.application();
    appComponent.inject(this);

    logger.info("Git Info: {}", gitInfoManager.getGitInfo());
    app.start();
  }

  private Config loadConfig() {

    Config baseConfig =
        ConfigFactory.systemEnvironmentOverrides().withFallback(ConfigFactory.load());

    Config organizations = loadOrganizations();
    Config merged = organizations.withFallback(baseConfig).resolve();
    ConfigRenderOptions renderOptions =
        ConfigRenderOptions.defaults()
            .setOriginComments(false)
            .setComments(false)
            .setFormatted(true);

    if (merged.hasPath(SERVICE_NAME)) {
      String renderedConfig = merged.root().get(SERVICE_NAME).render(renderOptions);
      String maskedConfig = maskPasswords(renderedConfig);
      logger.info("Using configuration\n{}", maskedConfig);
    } else {
      throw new ConfigException.Missing(SERVICE_NAME);
    }

    return merged;
  }

  private static Config loadOrganizations() {
    Path tenantsDir =
        Path.of(System.getProperty("config.file")).getParent().resolve("organizations");

    File[] files = Optional.ofNullable(tenantsDir.toFile().listFiles()).orElse(new File[0]);
    Config merged = ConfigFactory.empty();
    for (File f : files) {
      Config parsedConfig = ConfigFactory.parseFile(f);
      merged = parsedConfig.withFallback(merged);
    }

    return ConfigFactory.empty().withValue(SERVICE_NAME, merged.root());
  }

  /**
   * Masks password fields in configuration using regular expressions.
   *
   * @param configOutput The rendered configuration string
   * @return Configuration string with sensitive values masked
   */
  private static String maskPasswords(String configOutput) {
    String masked = configOutput;

    // Matches: "password" : "value" or "username" : "value"
    masked = masked.replaceAll("(\"(?:password|username)\"\\s*:\\s*\")([^\"]+)(\")", "$1***$3");

    return masked;
  }
}
