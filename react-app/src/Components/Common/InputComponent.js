import React, {Component} from "react";

class InputComponent extends Component {
    constructor(props) {
        super(props);
        this.state = {
            label: this.props.label,
            name: this.props.name,
            value: this.props.value,
            onChangeCallback: this.props.onChangeCallback
        }
        this.onInputChange = this.onInputChange.bind(this)
    }

    onInputChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;

        this.setState({
            value: value
        });
        this.state.onChangeCallback(name, value)
    }

    render() {
        return (
            <div className="form-group">
                <label htmlFor="name">{this.state.label}</label>
                <input className="form-control" id={this.state.name} name={this.state.name} type="text" value={this.state.value}
                       onChange={this.onInputChange}/>
            </div>
        )
    }
}

export default InputComponent