package org.example.mylittleapp.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.util.List;
import java.util.Objects;

@Configuration
public class WebMvcConfigurerConfig implements WebMvcConfigurer {


  @Override
  public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
    this.serveDirectory(registry, "/", "classpath:/static/");
  }

  private void serveDirectory(
      ResourceHandlerRegistry registry,
      String endpoint,
      String location
  ){
    String[] patterns = endpoint.endsWith("/")
        ? new String[]{endpoint.substring(0, endpoint.length() - 1), endpoint, endpoint + "**"}
        : new String[]{endpoint, endpoint + "/", endpoint + "/**"};

    registry.addResourceHandler(patterns)
        .addResourceLocations(location.endsWith("/") ? location : location + "/")
        .resourceChain(false)
        .addResolver(new PathResourceResolver(){

          @Override
          public Resource resolveResource(
              HttpServletRequest request,
              @NonNull String requestPath,
              @NonNull List<? extends Resource> locations,
              @NonNull ResourceResolverChain chain
          )
          {
            Resource resource = super.resolveResource(
                request,
                requestPath,
                locations,
                chain
            );

            if(Objects.nonNull(resource)) {
              return resource;
            }

            return super.resolveResource(request, "/index.html", locations, chain);
          }

        });
  }
}
