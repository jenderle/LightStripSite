package transfer;

/**
 * Contains information for one animation item.
 * @author Jonathan Enderle
 *
 */
public class AnimationItem {

	private int red;
	private int green;
	private int blue;
	
	private int displayTime;
	private int transitionTime;
	
	private TransitionType transitionType;
	
	public AnimationItem(String hexColor, int displayTime, int transferTime, String transitionType) throws Exception {
        red = Integer.valueOf( hexColor.substring( 1, 3 ), 16 );
        green = Integer.valueOf( hexColor.substring( 3, 5 ), 16 );
        blue = Integer.valueOf( hexColor.substring( 5, 7 ), 16 );
        
        this.displayTime = displayTime;
        this.transitionTime = transferTime;
        
        switch(transitionType) {
        case "type1":
        	this.transitionType = TransitionType.TYPE1;
        	break;
        case "type2":
        	this.transitionType = TransitionType.TYPE1;
        	break;
        case "type3":
        	this.transitionType = TransitionType.TYPE3;
        	break;
        default:
        	throw new Exception("Bad transition type.");
        }
	}
	
	public int getRed() {
		return red;
	}

	public int getGreen() {
		return green;
	}

	public int getBlue() {
		return blue;
	}

	public int getDisplayTime() {
		return displayTime;
	}

	public int getTransitionTime() {
		return transitionTime;
	}

	public TransitionType getTransitionType() {
		return transitionType;
	}

}
