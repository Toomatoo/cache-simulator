package pku.ca.replacement;
/**
 * LRU替换算法的实现
 * @author 刘思远
 *
 */
public class LRUselector {	
	public static int getOutnum(boolean []used) {
		int sel = -1;
		for(int i=0; i<used.length; i++) {
			if(!used[i])
				sel = i;
		}
		return sel;
	}
}