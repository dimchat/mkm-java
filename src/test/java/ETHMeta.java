
import java.util.Map;

import chat.dim.Address;
import chat.dim.Meta;
import chat.dim.NetworkType;

public class ETHMeta extends Meta {

    public ETHMeta(Map<String, Object> dictionary) {
        super(dictionary);
    }

    public Address generateAddress(NetworkType network) {
        if ((getVersion() & VersionBTC) != VersionBTC) {
            throw new ArithmeticException("meta version error");
        }
        // BTC, ExBTC
        return ETHAddress.generate(getKey().getData(), network);
    }

    static {
        Meta.register(Meta.VersionETH, ETHMeta.class);
        Meta.register(Meta.VersionExETH, ETHMeta.class);
    }
}
