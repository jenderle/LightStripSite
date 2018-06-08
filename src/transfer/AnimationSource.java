package transfer;

/**
 * Acts as an updateable source for AnimationSequences.
 * Really a glorified Java pointer.
 * 
 * @author Jonathan Enderle
 *
 */
public class AnimationSource {
	
	private AnimationSequence animationSequence;
	
	public AnimationSource(AnimationSequence animationSequence) {
		this.animationSequence = animationSequence;
	}
	
	public AnimationSequence getAnimationSequence() {
		return animationSequence;
	}
	
	public void setAnimationSequence(AnimationSequence animationSequence) {
		this.animationSequence = animationSequence;
	}

}
