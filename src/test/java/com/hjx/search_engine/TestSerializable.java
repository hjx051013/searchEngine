package com.hjx.search_engine;

import com.hjx.search_engine.entity.Demo;
import org.junit.Test;

import java.io.*;

public class TestSerializable {

    private void serialize(Object o) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("file.txt")));
        oos.writeObject(o);
        System.out.println("序列化成功");
        oos.close();
    }

    private Demo deserialize(String filename) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(filename)));
        Demo demo = (Demo)ois.readObject();
        ois.close();
        System.out.println("反序列化成功");
        return demo;
    }

    @Test
    public void test1() throws IOException, ClassNotFoundException {
        Demo demo = new Demo();
        demo.setId("123");
        demo.setName("hjx");
        serialize(demo);
        Demo demo1 = deserialize("file.txt");
        System.out.println(demo1.toString());
    }
}
