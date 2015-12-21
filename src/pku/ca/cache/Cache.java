package pku.ca.cache;

import java.util.ArrayList;

/**
* cache数据结构和逻辑
* @author 刘思远
*/

/**
 * 
 * 
 *
 */
public class Cache {
	/**
	 * 
	 * @Cachetable 项数为128
	 * @二路组相联
	 * 
	 * 初始设置，各行都是无效的；默认最近使用第0路中行
	 */
	
	
	public static final int sumofentry = 128;
	public static final int sumofgroup = 2;
	
	public ArrayList<ArrayList<CacheEntry>> cachetable;
	
	/*1：最近被使用了，0：最近没被使用*/
	boolean [][]numofNotused;
	
	public Cache() {
		cachetable = new ArrayList<ArrayList<CacheEntry>>();
		numofNotused = new boolean[sumofentry][sumofgroup];
		for(int t=0; t<sumofgroup; t++) {//第t组
			//先需要建立每路的ArrayList
			cachetable.add(new ArrayList<CacheEntry>());
			for(int i=0; i<sumofentry; i++) {//第i行
				//再建立每组中每行的Entry项
				cachetable.get(t).add(new CacheEntry(t));
			}
		}
		
		for(int i=0; i<sumofentry; i++) {//第i行
			//每组第一路认为是最近使用了的
			numofNotused[i][0] = true;
			numofNotused[i][1] = false;
		}
	}
	/**
	 * 查看某一路某一行cache的有效性
	 * @param numofgroup
	 * @param addrindex
	 * @return
	 */
	public boolean examValid(int numofgroup, int addrindex) {
		if(cachetable.get(numofgroup).get(addrindex).valid)
			return true;
		else
			return false;
	}
	/**
	 * 
	 * @param addrtag 访问地址的tag
	 * @param entry 找到的cache行
	 * @return 操作正确与否
	 */
	public boolean examTag(int addrtag, int group, int index) {
		if(cachetable.get(group).get(index).valid
				&& cachetable.get(group).get(index).tag == addrtag)
			return true;
		else
			return false;
	}
	/**
	 * 
	 * @param addrindex 访问地址的index
	 * @param numofgroup 请求的组号
	 * @param entry 返回的entry
	 * @return 操作正确与否
	 */
	public boolean getEntry(int addrindex, int numofgroup, CacheEntry entry) {
		if(numofgroup != 0 
				&& numofgroup != 1)
			return false;
		if(addrindex < 0
				|| addrindex >sumofentry-1)
			return false;
		
		entry.copyEntry(cachetable.get(numofgroup).get(addrindex));
		return true;
	}
	
	/**
	 * 
	 * @param offset cache 行内偏移
	 * @param entry 数据cache
	 * @param bytedata 返回一个byte数据
	 * @return 操作正确与否
	 */
	public boolean readEntryBytedata(int offset, int group, int index, byte []bytedata) {
		if(offset < 0
				|| offset >31) 
			return false;
		System.arraycopy(cachetable.get(group).get(index).cachebyte, offset, bytedata, 0, bytedata.length); 
		return true;
	}
	
	public boolean writeEntrybyte(int numofgroup, int addrindex, int offset, byte []writedata, int len) {
		byte data[] = new byte[len];
		System.arraycopy(writedata, 0, data, 0, len);
		if(cachetable.get(numofgroup).get(addrindex).setCacheEntrybyte(offset, data))
			return true;
		else
			return false;
	}
	
	
	public boolean writeEntry(int numofgroup, int addrindex, int tag, byte []writedata) {
		if(numofgroup != 0 
				&& numofgroup != 1)
			return false;
		if(addrindex < 0
				|| addrindex >sumofentry-1)
			return false;
		
		if(cachetable.get(numofgroup).get(addrindex).setCacheEntry(tag, true, writedata))
			return true;
		else
			return false;
	}
	
}