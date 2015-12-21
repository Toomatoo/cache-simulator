package pku.ca.util;

public class Request {
	public int op;//0是读，1是写，2是清除cache
	public int address;
	public byte []data;
	
	public int particle;//可以位8， 16， 32
	public int ext;//0为无符号扩展，1为有符号扩展
	
	public Request(int op, int address, int _data, //申请应该是32位来吗？？？
			int particle, int ext) {
		this.op = op;
		this.address = address;
		if(op == 1) {
			this.data = new byte[4];
			this.data[0] = (byte) (_data & 255);
			this.data[1] = (byte) ((_data>>8) & 255);
			this.data[2] = (byte) ((_data>>16) & 255);		
			this.data[3] = (byte) ((_data>>24) & 255);
		}
		this.particle = particle;
		this.ext = ext;
	}
}