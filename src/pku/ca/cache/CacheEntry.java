package pku.ca.cache;

/**
* cache表项
* @author 刘思远
*/

public class CacheEntry {
	int tag;
	boolean valid;//false是无效的，true是有效
	byte []cachebyte;
	boolean dirty;//false是干净的，true是脏的
	public int numofgroup; 
	
	static final int lenEntry = 32;
	
	
	
	public CacheEntry(int numofgroup) {
		this.tag = -1;
		this.valid = false;
		this.dirty = false;
		this.cachebyte = new byte[lenEntry];
		this.numofgroup = numofgroup;
	}
	/**
	 * @param tag region
	 * @param valid bit
	 * @param cachebyte one row of cache
	 */
	public boolean setCacheEntry(int tag, boolean valid, byte []cachebyte) {
		if(cachebyte.length != lenEntry)
			return false;
		this.tag = tag;
		this.valid = valid;
		System.arraycopy(cachebyte, 0, this.cachebyte, 0, lenEntry); 
		return true;
	} 
	public boolean setCacheEntrybyte(int offset, byte []cachebyte) {
		if(offset+cachebyte.length >lenEntry)
			return false;
		for(int i=0; i<cachebyte.length; i++)
			this.cachebyte[i+offset] = cachebyte[i];
		return true;
	}
	
	protected void copyEntry(CacheEntry entry){
		this.tag = entry.tag;
		this.valid = entry.valid;
		System.arraycopy(entry.cachebyte, 0, this.cachebyte, 0, entry.cachebyte.length);
		this.dirty = entry.dirty;
		this.numofgroup = entry.numofgroup; 
	}
}