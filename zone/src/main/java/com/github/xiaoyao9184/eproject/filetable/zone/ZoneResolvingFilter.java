package com.github.xiaoyao9184.eproject.filetable.zone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Fork from UAA IdentityZoneResolvingFilter
 * Created by xy on 2020/6/1.
 */
public class ZoneResolvingFilter extends OncePerRequestFilter implements InitializingBean {

    private Set<String> defaultZoneHostnames = new HashSet<>();
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String hostname = request.getServerName();
        String subdomain = getSubdomain(hostname);
        logger.debug("Find identity zone for subdomain " + subdomain);
        try {
            ZoneHolder.set(subdomain);
            filterChain.doFilter(request, response);
        } finally {
            ZoneHolder.clear();
        }
    }

    private String getSubdomain(String hostname) {
        String lowerHostName = hostname.toLowerCase();
        if (defaultZoneHostnames.contains(lowerHostName)) {
            return "";
        }
        for (String internalHostname : defaultZoneHostnames) {
            if (lowerHostName.endsWith("." + internalHostname)) {
                return lowerHostName.substring(0, lowerHostName.length() - internalHostname.length() - 1);
            }
        }
        //FileTable is catch all if we haven't configured anything
        if (defaultZoneHostnames.size()==1 && defaultZoneHostnames.contains("localhost")) {
            logger.debug("No root domains configured, FileTable is catch-all domain for host:"+hostname);
            return "";
        }
        logger.debug("Unable to determine subdomain for host:"+hostname+"; root domains:"+Arrays.toString(defaultZoneHostnames.toArray()));
        return null;
    }

    public void setAdditionalInternalHostnames(Set<String> hostnames) {
        if (hostnames!=null) {
            hostnames
                .stream()
                .forEach(
                  entry -> this.defaultZoneHostnames.add(entry.toLowerCase())
                 );
        }
    }

    public void setDefaultInternalHostnames(Set<String> hostnames) {
        if (hostnames!=null) {
            hostnames
                .stream()
                .forEach(
                        entry -> this.defaultZoneHostnames.add(entry.toLowerCase())
                );
        }
    }

    public synchronized void restoreDefaultHostnames(Set<String> hostnames) {
        this.defaultZoneHostnames.clear();
        if (hostnames!=null) {
            hostnames
                .stream()
                .forEach(
                        entry -> this.defaultZoneHostnames.add(entry.toLowerCase())
                );
        }
    }

    public Set<String> getDefaultZoneHostnames() {
        return new HashSet<>(defaultZoneHostnames);
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        logger.info("Zone Resolving Root domains are: "+ Arrays.toString(getDefaultZoneHostnames().toArray()));
    }
}
