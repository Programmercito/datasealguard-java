package org.osbo.core.datasealguard.validate;

import com.auth0.jwt.interfaces.DecodedJWT;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Pattern;
import org.osbo.core.datasealguard.exception.InvalidSignException;
import org.osbo.core.datasealguard.json.JsonUtil;
import org.osbo.core.datasealguard.pojo.ValidateObject;
import org.osbo.core.datasealguard.sign.HmacUtil;
import org.osbo.core.datasealguard.sign.JwtUtil;
import org.osbo.core.datasealguard.type.TypeSign;
import static org.osbo.core.datasealguard.type.TypeSign.HDAC;
import static org.osbo.core.datasealguard.type.TypeSign.JWT;

/**
 *
 * @author programmercito
 */
public class SignerDataValidate<T>{

    private TypeSign type;
    private int timeexpire;
    private String secret;

    public String forSend(ValidateObject data) {
        String token = null;
        switch (getType()) {
            case JWT:
                token = JwtUtil.signJWT(data, getSecret(), getTimeexpire());
                break;
            case HDAC:
                Map<String, String> map = HmacUtil.computeHMAC(data, getSecret());
                token = map.get("hash") + "|" + Base64.getUrlEncoder().encodeToString(map.get("mensaje").getBytes(StandardCharsets.UTF_8));
                break;
        }
        return token;
    }

    public T  extract(String token, Type classOfT) throws InvalidSignException {
        T fromJson = null;
        switch (getType()) {
            case JWT:
                DecodedJWT validateJWT = JwtUtil.validateJWT(token, getSecret());
                String este = validateJWT.getClaim("d").asString();
                fromJson = JsonUtil.fromJson(este, classOfT);
                break;

            case HDAC:
                String[] partes = token.split(Pattern.quote("|"));
                try {
                    HmacUtil.validateHMAC(partes[0], getSecret(), partes[1]);
                    String men = partes[1];
                    men = new String(Base64.getDecoder().decode(men), "UTF-8");
                    fromJson = JsonUtil.fromJson(men, classOfT);
                } catch (InvalidSignException ex) {
                    throw ex;
                } catch (Exception ex) {
                    throw new InvalidSignException("Error al tratar de validar");
                }
                break;
        }
        return fromJson;
    }

    /**
     * @return the type
     */
    public TypeSign getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public SignerDataValidate setType(TypeSign type) {
        this.type = type;
        return this;
    }

    /**
     * @return the timeexpire
     */
    public int getTimeexpire() {
        return timeexpire;
    }

    /**
     * @param timeexpire the timeexpire to set
     */
    public SignerDataValidate setTimeexpire(int timeexpire) {
        this.timeexpire = timeexpire;
        return this;
    }

    /**
     * @return the secret
     */
    public String getSecret() {
        return secret;
    }

    /**
     * @param secret the secret to set
     */
    public SignerDataValidate setSecret(String secret) {
        this.secret = secret;
        return this;
    }
}
