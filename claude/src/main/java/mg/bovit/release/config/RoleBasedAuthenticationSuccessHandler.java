package mg.bovit.release.config;

import java.io.IOException;
import java.util.Collection;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String targetUrl = resolveTargetUrl(authentication.getAuthorities());
        response.sendRedirect(request.getContextPath() + targetUrl);
    }

    private String resolveTargetUrl(Collection<? extends GrantedAuthority> authorities) {
        boolean isAdmin = hasRole(authorities, "ROLE_ADMIN");
        if (isAdmin) {
            return "/bovins";
        }
        if (hasRole(authorities, "ROLE_VENTE")) {
            return "/vente/list";
        }
        if (hasRole(authorities, "ROLE_PESEE")) {
            return "/peseBovin/list";
        }
        if (hasRole(authorities, "ROLE_LOT")) {
            return "/bovins";
        }
        if (hasRole(authorities, "ROLE_STOCK")) {
            return "/materiel/liste";
        }
        if (hasRole(authorities, "ROLE_CAISSE")) {
            return "/caisse/stats";
        }
        if (hasRole(authorities, "ROLE_EMPLOYE")) {
            return "/employee/list";
        }
        return "/auth/login?error=true";
    }

    private boolean hasRole(Collection<? extends GrantedAuthority> authorities, String role) {
        return authorities.stream().anyMatch(authority -> role.equals(authority.getAuthority()));
    }
}