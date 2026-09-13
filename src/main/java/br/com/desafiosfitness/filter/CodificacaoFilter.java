package br.com.desafiosfitness.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

/**
 * Garante UTF-8 em toda requisicao/resposta, evitando problemas de acentuacao
 * nos formularios (nome, descricao, etc).
 */
@WebFilter("/*")
public class CodificacaoFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }
}
