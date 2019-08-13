
import java.util.Map;

import chat.dim.mkm.Address;
import chat.dim.mkm.Meta;
import chat.dim.mkm.NetworkType;

public class ETHMeta extends Meta {

    public ETHMeta(Map<String, Object> dictionary) throws NoSuchFieldException, ClassNotFoundException {
        super(dictionary);
    }

    public Address generateAddress(NetworkType network) {
        if ((version & VersionBTC) != VersionBTC) {
            throw new ArithmeticException("meta version error");
        }
        // BTC, ExBTC
        return ETHAddress.generate(key.getData(), network);
    }

    static {
        Meta.register(Meta.VersionETH, ETHMeta.class);
        Meta.register(Meta.VersionExETH, ETHMeta.class);
    }
}
