package com.sos.smartopenspace.config

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProviders
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.format.support.FormattingConversionService
import org.springframework.web.servlet.config.annotation.*
import org.springframework.web.servlet.resource.AbstractResourceResolver
import org.springframework.web.servlet.resource.ResourceResolverChain
import org.springframework.web.servlet.resource.ResourceUrlProvider
import javax.servlet.http.HttpServletRequest


@Configuration
@EnableAutoConfiguration
//@EnableWebMvc
class WebConfig : WebMvcConfigurer {
  override fun addCorsMappings(registry: CorsRegistry) {
    registry
      .addMapping("/**")
      .allowedMethods("GET", "PUT", "POST")
      .allowedOrigins("http://localhost:1234", "https://smartopenspace.herokuapp.com")
  }

//  override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
//    println("*********************addResourceHandlers")
//    registry
//      .addResourceHandler("/**")
//      .resourceChain(false)
//      .addResolver(object : AbstractResourceResolver() {
//        override fun resolveResourceInternal(
//          request: HttpServletRequest?,
//          requestPath: String,
//          locations: List<Resource>,
//          chain: ResourceResolverChain
//        ): Resource? {
//          println("*********************resolveResourceInternal")
//          return chain.resolveResource(request, "index.html", locations)
//        }
//
//        override fun resolveUrlPathInternal(
//          resourceUrlPath: String,
//          locations: List<Resource>,
//          chain: ResourceResolverChain
//        ): String? {
//          println("*********************resolveUrlPathInternal")
//          return chain.resolveUrlPath(resourceUrlPath, locations)
//        }
//      });
//  }
}
