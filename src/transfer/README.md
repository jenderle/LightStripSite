# TRANSFER README #

### Hierarchy of items: ###

* AnimationSource
* AnimationSequence
* AnimationStep

(This is where the code changes focus; from object-based Java to LED-facing C-code)

* Frame
* Pixel


### Explanation: ###

* `AnimationSource` - Basically an updateable pointer to an `AnimationSequence`.
* `AnimationSequence` - The full sequence of steps.
* `AnimationStep` - one stage in a series of animations. It will take many `frame`s to cycle through one `AnimationStep`
* `Frame` - one set of pixels that an LED strip should display.
* `Pixel` - one individually addressable pixel on an LED strip.
