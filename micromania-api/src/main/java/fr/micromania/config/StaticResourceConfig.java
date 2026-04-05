package fr.micromania.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${app.media.images-path:./media/images}")
    private String imagesPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path resolvedPath = resolveImagesPath();

        String location = resolvedPath.toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }

        log.info("Images servies depuis : {}", location);

        registry.addResourceHandler("/images/**")
                .addResourceLocations(location);
    }

    private Path resolveImagesPath() {
        Path configured = Paths.get(imagesPath).normalize();

        if (configured.isAbsolute()) {
            return configured.toAbsolutePath().normalize();
        }

        Path cwd = Paths.get("").toAbsolutePath().normalize();

        Path candidate = cwd.resolve(configured).normalize();
        if (Files.exists(candidate)) {
            return candidate;
        }

        Path parent = cwd.getParent();
        if (parent != null) {
            Path parentCandidate = parent.resolve(configured).normalize();
            if (Files.exists(parentCandidate)) {
                return parentCandidate;
            }
        }

        return candidate;
    }
}