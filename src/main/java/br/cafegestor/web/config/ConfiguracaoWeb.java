package br.cafegestor.web.config;

import br.cafegestor.web.seguranca.AutorizacaoInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfiguracaoWeb implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registro) {
        registro.addInterceptor(new AutorizacaoInterceptor()).addPathPatterns("/api/**");
    }
}
