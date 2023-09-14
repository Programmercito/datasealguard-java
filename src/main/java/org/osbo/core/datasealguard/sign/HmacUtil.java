package org.osbo.core.datasealguard.sign;

/**
 *
 * @author programmercito
 */
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.osbo.core.datasealguard.exception.InvalidSignException;
import org.osbo.core.datasealguard.json.JsonUtil;
import org.osbo.core.datasealguard.pojo.ValidateObject;
import org.osbo.core.datasealguard.type.TypeSign;

public class HmacUtil {

    private static final String HMAC_ALGO = "HmacSHA256";

    public static Map<String, String> computeHMAC(ValidateObject data, String secretKey) {
        String menssaje = JsonUtil.convert(data);
        Map<String, String> mapa = new HashMap<String, String>();
        mapa.put("hash", computeHMAC(menssaje, secretKey));
        mapa.put("mensaje", menssaje);
        return mapa;
    }

    public static String computeHMAC(String message, String secretKey) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGO);
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute HMAC", e);
        }
    }

    public static boolean validateHMAC(String message, String secretKey, String originalHMAC) throws InvalidSignException {
        String computedHMAC = computeHMAC(message, secretKey);
        if (!computedHMAC.equals(originalHMAC)) {
            throw new InvalidSignException("Firma invalida el mensaje de seguridad es invalido,"
                    + "no cuenta con permisos");
        } else {
            return true;
        }
    }

}
