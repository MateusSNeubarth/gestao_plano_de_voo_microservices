package com.projarc.trabalhofinal.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.HttpHeadersFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

/**
 * Autenticação e autorização só no gateway: os serviços ficam na rede interna do compose,
 * sem porta publicada, e recebem a identidade nos cabeçalhos X-Usuario e X-Papel.
 */
@Configuration
public class SegurancaConfig {

    static final String CABECALHO_USUARIO = "X-Usuario";
    static final String CABECALHO_PAPEL = "X-Papel";
    private static final RequestAttributeSecurityContextRepository CONTEXTOS = new RequestAttributeSecurityContextRepository();

    private static final String[] CADASTROS = {
            "/malha/aeroportos/**", "/malha/aerovias/**",
            "/gestao/pilotos/**", "/gestao/aeronaves/**"
    };

    @Bean
    SecurityFilterChain seguranca(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/*/v3/api-docs", "/actuator/health", "/fallback/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, CADASTROS).authenticated()
                        .requestMatchers(CADASTROS).hasRole("ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    UserDetailsService usuarios(@Value("${seguranca.usuarios.admin.username}") String admin,
                                @Value("${seguranca.usuarios.admin.password}") String senhaAdmin,
                                @Value("${seguranca.usuarios.usuario.username}") String usuario,
                                @Value("${seguranca.usuarios.usuario.password}") String senhaUsuario) {
        PasswordEncoder codificador = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return new InMemoryUserDetailsManager(
                User.withUsername(admin).password(codificador.encode(senhaAdmin)).roles("ADMIN").build(),
                User.withUsername(usuario).password(codificador.encode(senhaUsuario)).roles("USUARIO").build());
    }

    // aplicado a toda requisição repassada: o cliente não forja a identidade e a senha não entra na rede interna
    @Bean
    HttpHeadersFilter.RequestHttpHeadersFilter identidade() {
        return (entrada, request) -> {
            HttpHeaders saida = new HttpHeaders();
            saida.addAll(entrada);
            saida.remove(HttpHeaders.AUTHORIZATION);
            saida.remove(CABECALHO_USUARIO);
            saida.remove(CABECALHO_PAPEL);
            // o CircuitBreaker roda a chamada em outra thread, então o contexto vem do atributo da requisição, não do SecurityContextHolder
            Authentication autenticacao = CONTEXTOS.loadDeferredContext(request.servletRequest()).get().getAuthentication();
            if (autenticacao != null && !(autenticacao instanceof AnonymousAuthenticationToken)) {
                saida.set(CABECALHO_USUARIO, autenticacao.getName());
                autenticacao.getAuthorities().stream()
                        .map(a -> a.getAuthority().replaceFirst("^ROLE_", ""))
                        .findFirst()
                        .ifPresent(papel -> saida.set(CABECALHO_PAPEL, papel));
            }
            return saida;
        };
    }
}
