package com.ofg.infrastructure.property;

import com.ofg.infrastructure.discovery.ServiceConfigurationResolver;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class AppCoordinates {

    public static final String CONFIG_FOLDER = "CONFIG_FOLDER";
    public static final String APP_ENV = "APP_ENV";

    private final String environment;
    private final String applicationName;
    private final String countryCode;

    public static AppCoordinates defaults(Resource microserviceConfigResource) {
        requireNonNull(microserviceConfigResource, " Microservice configuration cannot be null");
        try {
            final String configJson = IOUtils.toString(microserviceConfigResource.getURL());
            final ServiceConfigurationResolver configurationResolver = new ServiceConfigurationResolver(configJson);
            final String appName = configurationResolver.getMicroserviceName();
            final String countryName = configurationResolver.getBasePath();
            return new AppCoordinates(findEnvironment(), appName, countryName);
        } catch (IOException e) {
            throw new IllegalStateException("Can't read " + microserviceConfigResource, e);
        }
    }

    AppCoordinates(String environment, String applicationName, String countryCode) {
        this.environment = requireNonNull(environment);
        this.applicationName = requireNonNull(applicationName);
        this.countryCode = requireNonNull(countryCode);
    }

    public File getConfigFolder(File rootFolder) {
        File folder = new File(rootFolder, environment);
        final String[] components = nameComponents();
        for (int i = 0; i < components.length - 1; i++) {
            folder = new File(folder, components[i]);
        }
        return useParentIfLastChildIsCountry(folder);
    }

    private File useParentIfLastChildIsCountry(File folder) {
        if (folder.getName().equals(countryCode)) {
            return folder.getParentFile();
        } else {
            return folder;
        }
    }

    private static String findEnvironment() {
        final String envOrNull = PropertyUtils.getProperty(APP_ENV, null);
        return requireNonNull(envOrNull, "No " + APP_ENV + " property found. Good candidates are: 'dev', 'prod-01', etc.");
    }

    public String getEnvironment() {
        return environment;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public List<File> getConfigFiles(File rootConfigFolder) {
        final String coreName = findBaseNameWithoutCountrySuffix(findBaseName());
        final File root = getConfigFolder(rootConfigFolder);
        final File regional = getRegionalConfigFolderFromAppConfigFolder(root);
        return Arrays.asList(
                new File(root, coreName + ".properties"),
                new File(root, coreName + ".yaml"),
                new File(regional, coreName + "-" + countryCode + ".properties"),
                new File(regional, coreName + "-" + countryCode + ".yaml"));
    }

    private String findBaseName() {
        final String[] nameComponents = nameComponents();
        return nameComponents[nameComponents.length - 1];
    }

    private String[] nameComponents() {
        return applicationName.split("/");
    }

    private String findBaseNameWithoutCountrySuffix(String baseName) {
        if (baseName.endsWith(countryCode)) {
            final int suffixLength = countryCode.length() + 1;
            return baseName.substring(0, baseName.length() - suffixLength);
        } else {
            return baseName;
        }
    }

    private File getRegionalConfigFolderFromAppConfigFolder(File appConfigFolder) {
        return new File(appConfigFolder, countryCode);
    }
}
