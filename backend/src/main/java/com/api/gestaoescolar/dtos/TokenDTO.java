package com.api.gestaoescolar.dtos;

import java.io.Serializable;
import java.util.Date;

public class TokenDTO implements Serializable {
    
    private static final long serialVersionUID =1L;

    private String cpf;
    private String userType;
    private Boolean authenticateBoolean;
    private Date created;
    private Date expiration;
    private String accessToken;
    private String refreshToken;
    
    public TokenDTO(){}
    
    public TokenDTO(String cpf, Boolean authenticateBoolean, Date created, Date expiration, String accessToken,String refreshToken) {
        this.cpf = cpf;
        this.authenticateBoolean = authenticateBoolean;
        this.created = created;
        this.expiration = expiration;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public Boolean getAuthenticateBoolean() {
        return authenticateBoolean;
    }
    public void setAuthenticateBoolean(Boolean authenticateBoolean) {
        this.authenticateBoolean = authenticateBoolean;
    }
    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }
    public Date getExpiration() {
        return expiration;
    }
    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
    public String getAcessToken() {
        return accessToken;
    }
    public void setAcessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cpf == null) ? 0 : cpf.hashCode());
        result = prime * result + ((authenticateBoolean == null) ? 0 : authenticateBoolean.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((expiration == null) ? 0 : expiration.hashCode());
        result = prime * result + ((accessToken == null) ? 0 : accessToken.hashCode());
        result = prime * result + ((refreshToken == null) ? 0 : refreshToken.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TokenDTO other = (TokenDTO) obj;
        if (cpf == null) {
            if (other.cpf != null)
                return false;
        } else if (!cpf.equals(other.cpf))
            return false;
        if (authenticateBoolean == null) {
            if (other.authenticateBoolean != null)
                return false;
        } else if (!authenticateBoolean.equals(other.authenticateBoolean))
            return false;
        if (created == null) {
            if (other.created != null)
                return false;
        } else if (!created.equals(other.created))
            return false;
        if (expiration == null) {
            if (other.expiration != null)
                return false;
        } else if (!expiration.equals(other.expiration))
            return false;
        if (accessToken == null) {
            if (other.accessToken != null)
                return false;
        } else if (!accessToken.equals(other.accessToken))
            return false;
        if (refreshToken == null) {
            if (other.refreshToken != null)
                return false;
        } else if (!refreshToken.equals(other.refreshToken))
            return false;
        return true;
    }
    
    
}