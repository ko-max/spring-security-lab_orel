package ua.kpi.its.lab.security.config

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.text.SimpleDateFormat

@Configuration
@EnableWebMvc
class WebConfig : WebMvcConfigurer {

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        val builder = Jackson2ObjectMapperBuilder()
            .indentOutput(true)
            .dateFormat(SimpleDateFormat("yyyy-MM-dd"))
            .modulesToInstall(KotlinModule.Builder().build())

        converters
            .add(MappingJackson2HttpMessageConverter(builder.build()))
    }
}