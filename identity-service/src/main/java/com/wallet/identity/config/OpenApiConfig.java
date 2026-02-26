package com.wallet.identity.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Identity Service API", description = "Digital Wallet Platform - Identity and Authentication Service API Documentation", version = "1.0", contact = @Contact(name = "Digital Wallet Team", email = "support@digitalwallet.com", url = "https://digitalwallet.com"), license = @License(name = "Proprietary", url = "https://digitalwallet.com/license")))
public class OpenApiConfig {
}
