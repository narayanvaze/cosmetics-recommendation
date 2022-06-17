package com.cosmopredictor.model;
import java.sql.Blob;
import java.sql.SQLException;
import javax.persistence.*;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

@Entity
@Table(name = "image_table")
public class ImageModel {
	public ImageModel() {
		super();
	}
	private byte[] picByte;

	
	public ImageModel(String name, String type, byte[] picByte) throws SerialException, SQLException {
		this.name = name;
		this.type = type;
		this.picBlob = byteToBlob(picByte);
	}

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "type")
	private String type;

    //image bytes can have large lengths so we specify a value
    //which is more than the default length for picByte column
//	@Column(name = "picByte", length = 1000)
//	private byte[] picByte;
	@Column(name = "picBlob")
	private Blob picBlob;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public byte[] getPicByte() {
		return picByte;
	}

	public void setPicBlob(byte[] picByte) throws SerialException, SQLException {
		this.picBlob = byteToBlob(picByte);
	}
	
	public Blob byteToBlob(byte [] arr) throws SerialException, SQLException {
		 Blob blob = null;
//		 for(int i=0; i< arr.length; i++ ) {
		 blob = new SerialBlob(arr);
//		 }
		 return blob;
	}
}
