package transfer;

/**
 * Acts as an updateable source for AnimationSequences.
 * Really a glorified Java pointer.
 * 
 * @author Jonathan Enderle
 *
 */
public class AnimationSource {
	
	private volatile AnimationSequence animationSequence;
	
	public AnimationSequence getAnimationSequence() {
		return animationSequence;
	}
	
	public void setAnimationSequence(AnimationSequence animationSequence) {
		this.animationSequence = animationSequence;
	}

}
