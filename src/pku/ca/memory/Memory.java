package pku.ca.memory;

/**
 * 内存的模拟
 * @author 刘思远
 *
 */
public class Memory {
	
	public static final int lengthofMemory = 65536;
	public byte []memorybyte;
	
	public Memory() {
		memorybyte = new byte[lengthofMemory];
	}
	
	/**
	 * 
	 * @param addr
	 * @param len
	 * @return byte[address+length]
	 */
	public boolean getBytes(int addr, int len, byte []retbyte) {
		if(addr<0 || addr>lengthofMemory)
			return false;
		if(addr+len-1>lengthofMemory)
			return false;
		if(retbyte.length < len)
			return false;
		
		System.arraycopy(memorybyte, addr, retbyte, 0, len);
		return true;
	}
	
	/**
	 * 
	 * @param addr 写的起始位置
	 * @param len 写长度
	 * @param indata 写入数据
	 * @return 返回成功与否
	 */
	public boolean setBytes(int addr, int len, byte []indata) {
		if(addr<0 || addr>lengthofMemory)
			return false;
		if(addr+len-1>lengthofMemory)
			return false;
		
		System.arraycopy(indata, 0, memorybyte, addr, len);
		return true;
	}
}