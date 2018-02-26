# Reactive
Plugin for legui adding react like api for building user interfaces

### Code example

This is a small component that shows the number of times it has been clicked, but the important part is what you don't see,
there is no code to change the label text, it updates itself when the state of the component changes!
```kotlin
data class DemoState(val count: Int) : RState

class DemoComponent : RComponent<EmptyProps, DemoState>() {

    override fun getInitialState() = DemoState(0)

    override fun RBuilder.render() {
        
        label("You clicked me ${state.count} times!"){
        
            style {
                sizeX = 150f
                sizeY = 30f
        
                horizontalAlign = HorizontalAlign.CENTER
        
                backgroundColor { ColorConstants.lightBlue() }
                borderless()
            }
        
            postMount {
                center()
            }
        
            onClick {
                setState { DemoState(count + 1) }
            }
        }
    }
}
```