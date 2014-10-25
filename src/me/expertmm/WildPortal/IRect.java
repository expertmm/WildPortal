/**
 * 
 */
package me.expertmm.WildPortal;

/**
 * @author Jacob Gustafson
 *
 */
public class IRect {
	public int X=0;
	public int Y=0;
	public int Width=1;
	public int Height=1;
	
	public IRect(int set_X, int set_Y, int set_Width, int set_Height) {
		this.X=set_X;
		this.Y=set_Y;
		this.Width=set_Width;
		this.Height=set_Height;
	}
	
	public int getLeft() {
		return this.X;
	}
	public int getTop() {
		return this.Y;
	}
	public int getRight() {
		return this.X+this.Width;
	}
	public int getRightInclusive() {
		return (this.Width>0)?this.X+this.Width-1:this.X; //for example if width is 1, return X
	}
	public int getBottom() {
		return this.Y+this.Height;
	}
	public int getBottomInclusive() {
		return (this.Height>0)?this.Y+this.Height-1:this.Y; //for example if height is 1, return X
	}
	public void SetInclusive(int set_Left, int set_Top, int set_Right, int set_Bottom) {
		setRightInclusive(set_Left,set_Right);
		setBottomInclusive(set_Top,set_Bottom);
	}
	public void setRight(int set_Left, int set_Right) {
		this.X=set_Left;
		this.Width=set_Right-set_Left;
	}
	public void setRightInclusive(int set_Left, int set_Right) {
		this.X=set_Left;
		this.Width=set_Right-set_Left+1;
	}
	public void setBottom(int set_Top, int set_Bottom) {
		this.Y=set_Top;
		this.Width=set_Bottom-set_Top;
	}
	public void setBottomInclusive(int set_Top, int set_Bottom) {
		this.Y=set_Top;
		this.Width=set_Bottom-set_Top+1;
	}
	public String toStringXYWidthHeight(String delimiter) {
		if (delimiter==null) {
			delimiter=",";
			main.logWriteLine("WARNING: Delimiter was null in toStringXYWidthHeight, so used '"+delimiter+"' instead.");
		}
		return String.valueOf(X)
				+delimiter+String.valueOf(Y)
				+delimiter+String.valueOf(Width)
				+delimiter+String.valueOf(Height);
	}
}
