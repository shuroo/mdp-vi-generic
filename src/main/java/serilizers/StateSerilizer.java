//package serilizers;
//
//import com.google.gson.Gson;
//import mdp.generic.State;
//import org.ehcache.spi.serialization.Serializer;
//import org.ehcache.spi.serialization.SerializerException;
//
//import java.nio.ByteBuffer;
//import java.nio.charset.Charset;
//
//public class StateSerilizer extends Serializer {
//
//    public StateSerilizer(ClassLoader classLoader){};
//
//    public ByteBuffer serialize(Object o) throws SerializerException {
//        Gson gson = new Gson();
//        String jsonObj = gson.toJson(o);
//        return ByteBuffer.wrap(jsonObj.getBytes(new Charset()));
//    }
//
//    public Object read(ByteBuffer byteBuffer) throws ClassNotFoundException, SerializerException {
//        return null;
//    }
//
//    public boolean equals(Object o, ByteBuffer byteBuffer) throws ClassNotFoundException, SerializerException {
//        return ((State)o).getId() == ((State)this).getId();
//    }
//}
//
//
//
