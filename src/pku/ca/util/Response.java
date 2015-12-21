package pku.ca.util;

public class Response {
	public int op;//0是读，1是写，2是清除cache
	public int address;
	public int data;//写操作的返回，直接返回数据或者扩展以后的结果
	
	public int particle;//可以位8， 16， 32
	public int ext;//0为无符号扩展，1为有符号扩展
	
	public void ResponseSet(int op, int address, int _data, //申请应该是32位来吗？？？
			int particle, int ext) {
		this.op = op;
		this.address = address;
		
		this.data = _data;
		
		this.particle = particle;
		this.ext = ext;
	}
}