package com.meus_projetos.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.meus_projetos.todolist.model.UserModel;
import com.meus_projetos.todolist.repository.IUserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Result;
// import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
// import jakarta.servlet.ServletRequest;
// import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

                // Aplica o filtro somente nas rotas de task
                String servletPath = req.getServletPath();
                System.out.println(servletPath);
                if (!servletPath.startsWith("/tasks/")) {
                    chain.doFilter(req, res);
                    return;
                }
                
                // Pegar autenticação básica (usuário e senha)
                var authorization = req.getHeader("Authorization");
                String encodedCredentials = authorization.substring("Basic".length()).trim();
                byte[] decodedCredentials = Base64.getDecoder().decode(encodedCredentials);
                String[] credentials = new String(decodedCredentials).split(":");
                String username = credentials[0];
                String password = credentials[1];
                
                // Validar usuário
                UserModel user = this.userRepository.findByUsername(username);
                // System.out.print("User no filter -> ");
                // System.out.println(user);
                if (user == null) {
                    System.out.println("Nome de usuário errado");
                    res.sendError(HttpStatus.UNAUTHORIZED.value());
                    return;
                }
                
                // Validar senha
                Result isCorrectPasword = BCrypt.verifyer().verify(
                    password.toCharArray(),
                    user.getPassword()
                );
                if (!isCorrectPasword.verified) {
                    System.out.println("Senha errada");
                    res.sendError(HttpStatus.UNAUTHORIZED.value());
                    return;
                }
                    
                req.setAttribute("idUser", user.getId());
                chain.doFilter(req, res);
    }
    
}
