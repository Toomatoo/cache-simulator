package pku.ca.addrparser;

/**
* Address Parser 
* @author 刘思远
*/
public class AddrParser {
	//内存地址原码
	private int address;
	//地址解析的结果：tag-4 index-7 offset-5
	private int tag;
	private int index;
	private int offset;
	
	/**
	 * 
	 * @param addr:地址
	 */
	public void setAddr(int addr) {
		this.address = addr;
		this.tag = addr >>> 12;
		this.index = (addr>>>5) & 127;
		this.offset = addr & 31;
	}
	/**
	 * 
	 * @return address's tag
	 */
	public int getAddress() {
		return this.address;
	}
	/**
	 * 
	 * @return address's tag
	 */
	public int getTag() {
		return this.tag;
	}
	/**
	 * 
	 * @return address's index
	 */
	public int getIndex() {
		return this.index;
	}
	/**
	 * 
	 * @return address'offset
	 */
	public int getOffset() {
		return this.offset;
	}
}