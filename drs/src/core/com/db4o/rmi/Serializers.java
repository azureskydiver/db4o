package com.db4o.rmi;

import java.io.*;
import java.util.*;


public class Serializers {

	private static final Map<Class<?>, Serializer<?>> serializers = new HashMap<Class<?>, Serializer<?>>();

	public static <T> Serializer<T> addSerializer(Serializer<T> serializer, Class<?>... classes) {
		for (Class<?> clazz : classes) {
			serializers.put(clazz, serializer);
		}
		return serializer;
	}

	@SuppressWarnings("unchecked")
	public static <T> Serializer<T> serializerFor(Class<T> t) {
		return (Serializer<T>) serializers.get(t);
	}


	public final static Serializer<UUID> uuid = addSerializer(new Serializer<UUID>() {
		
		public UUID deserialize(DataInput in) throws IOException {
			return new UUID(in.readLong(), in.readLong());
		}

		public void serialize(DataOutput out, UUID id) throws IOException {
			out.writeLong(id.getMostSignificantBits());
			out.writeLong(id.getLeastSignificantBits());
		}

	}, UUID.class);

	public final static Serializer<Boolean> bool = addSerializer(new Serializer<Boolean>() {
		
		public Boolean deserialize(DataInput in) throws IOException {
			return in.readBoolean();
		}

		public void serialize(DataOutput out, Boolean v) throws IOException {
			out.writeBoolean(v);
		}

	}, Boolean.class, boolean.class);

	
	public final static Serializer<Integer> integer = addSerializer(new Serializer<Integer>() {

		public void serialize(DataOutput out, Integer item) throws IOException {
			out.writeInt(item);
		}

		public Integer deserialize(DataInput in) throws IOException {
			return in.readInt();
		}
	}, Integer.class, int.class);

	
	public final static Serializer<Long> sixtyfourbit = addSerializer(new Serializer<Long>() {

		public void serialize(DataOutput out, Long item) throws IOException {
			out.writeLong(item);
		}

		public Long deserialize(DataInput in) throws IOException {
			return in.readLong();
		}
	}, Long.class, long.class);

	
	public final static Serializer<Byte> eightbit = addSerializer(new Serializer<Byte>() {

		public void serialize(DataOutput out, Byte item) throws IOException {
			out.writeByte(item);
		}

		public Byte deserialize(DataInput in) throws IOException {
			return in.readByte();
		}
	}, Byte.class, byte.class);

	
	public final static Serializer<String> string = addSerializer(new Serializer<String>() {

		public void serialize(DataOutput out, String item) throws IOException {
			out.writeUTF(item);
		}

		public String deserialize(DataInput in) throws IOException {
			return in.readUTF();
		}
	}, String.class);

	
}
