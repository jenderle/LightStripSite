package transfer;

/**
 * Contains information for one animation item.
 * @author Jonathan Enderle
 *
 */
public class AnimationStep {

	private int red;
	private int green;
	private int blue;
	
	private int displayTime;
	private int transitionTime;
	
	private TransitionType transitionType;
	
	public AnimationStep(String hexColor, int displayTime, int transferTime, String transitionType) throws Exception {
        red = Integer.valueOf( hexColor.substring( 1, 3 ), 16 );
        green = Integer.valueOf( hexColor.substring( 3, 5 ), 16 );
        blue = Integer.valueOf( hexColor.substring( 5, 7 ), 16 );
        
        this.displayTime = displayTime;
        this.transitionTime = transferTime;
        
        switch(transitionType) {
        case "Jump":
        	this.transitionType = TransitionType.JUMP;
        	break;
        case "Fade":
        	this.transitionType = TransitionType.FADE;
        	break;
        case "Sweep":
        	this.transitionType = TransitionType.SWEEP;
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
