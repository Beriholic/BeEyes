package xyz.beriholic.beeyes.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(type = SecuritySchemeType.HTTP, scheme = "Bearer",
        name = "Authorization", in = SecuritySchemeIn.HEADER)
@OpenAPIDefinition(security = {@SecurityRequirement(name = "Authorization")})
public class SwaggerConfiguration {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("BeEyes API 文档")
                        .description("欢迎来到本项目API测试文档，在这里可以快速进行接口调试")
                        .version("1.0")
                        .license(new License()
                                .name("项目开源地址")
                                .url("https://github.com/Beriholic/BeEyes")
                        )
                )
                .externalDocs(new ExternalDocumentation()
                        .description("我的博客")
                        .url("https://beriholic.xyz")
                );
    }
}
