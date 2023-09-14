package org.osbo.core.datasealguard.sign;

/**
 *
 * @author programmercito
 */
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.util.Date;
import org.osbo.core.datasealguard.exception.InvalidSignException;
import org.osbo.core.datasealguard.json.JsonUtil;
import org.osbo.core.datasealguard.pojo.ValidateObject;
import org.osbo.core.datasealguard.type.TypeSign;

public class JwtUtil {

    public static String signJWT(ValidateObject type, String secretey, int minutes) {
        String menssaje = JsonUtil.convert(type);
        return signJWT(menssaje, secretey, minutes);
    }

    public static String signJWT(String mensaje, String secretKey, int minutes) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        // Crear un JWT con los claims proporcionados.
        // Además, agregamos la fecha de emisión y expiración.
        com.auth0.jwt.JWTCreator.Builder jwtBuilder = JWT.create()
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + minutes * 60 * 1000));  // 1 hora de expiración

        jwtBuilder.withClaim("d", mensaje);

        return jwtBuilder.sign(algorithm);
    }

    public static DecodedJWT validateJWT(String token, String secretKey) throws InvalidSignException {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException exception) {
            throw new InvalidSignException("Error al validar JWT");
        }
    }

}
