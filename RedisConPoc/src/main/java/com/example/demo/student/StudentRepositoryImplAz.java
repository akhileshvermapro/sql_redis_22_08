package com.example.demo.student;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.demo.student.repo.Student;
import com.example.demo.student.repo.StudentRepository;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
@Profile("azureRedis")
public class StudentRepositoryImplAz implements StudentRepository {
	
	private static final String KEY = "student";
	

	@Autowired
	private JedisPool pool;
	
	
	@Override
	public String saveStudent(Student student) {
		String res="saved";
		try(Jedis jedis = pool.getResource()){
			jedis.hset(this.getByteArray("student"), this.getByteArray(student.getId()), this.getByteArray(student));	
		}catch(Exception e){
			res = "Exception::---"+e.toString();			
		}
		return res;		
	}
	
	@Override
	public void deleteStudent(String id) throws IOException{
		try(Jedis jedis = pool.getResource()){
			jedis.hdel(getByteArray(KEY),getByteArray(id));		
		}catch(Exception e){
						
		}
	}
	
	@Override
	public Map<String, Student> findAllStudent() throws IOException, ClassNotFoundException{
		
		Map<String, Student> allStudentMap = null;
		try(Jedis jedis = pool.getResource()){
			allStudentMap = this.getStudentMap(jedis.hgetAll(this.getByteArray(KEY)));
		}catch(Exception e){
			Student st = new Student();
			st.setName(e.toString());
			allStudentMap.put("Exception::",st);			
		}
		return allStudentMap;
	}
	
	@Override
	public  Student findStudent(Student student) throws IOException, ClassNotFoundException {
		Student st = null;
		try(Jedis jedis = pool.getResource()){
			st = (Student)this.getObject(jedis.hget(this.getByteArray(KEY), getByteArray(student.getId())));
		}catch(Exception e){
			st.setName("Exception -- "+e.toString());						
		}
		return st;
	}

	
	//operation specific to student
	private Map<String, Student> getStudentMap(Map<byte[], byte[]> map) throws ClassNotFoundException, IOException{
		Entry<byte[], byte[]> entries=null;
		Map<String, Student> resultMap = new HashMap<>();
		Set<Entry<byte[], byte[]>> set =  map.entrySet();
		Iterator<Entry<byte[], byte[]>> iterator = set.iterator();
		while(iterator.hasNext()){
			entries = iterator.next();
			resultMap.put((String)getObject(entries.getKey()),(Student)getObject(entries.getValue()));
		}
		
		return resultMap;
	}
	
	
	//utility
	private byte[] getByteArray(Object o) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try{
			out = new ObjectOutputStream(bos);
			out.writeObject(o);
			out.flush();			
		}finally{
			bos.close();
		}
		return bos.toByteArray();
	}
	
	private Object getObject(byte[] b) throws IOException, ClassNotFoundException{
		ByteArrayInputStream bis = new ByteArrayInputStream(b);
		ObjectInput in = null;
		Object o=null;
		try{
			in = new ObjectInputStream(bis);
			o=in.readObject();
		}
		finally{
			if(in!=null){
				in.close();
			}
		}
		return o;
	}	


}
