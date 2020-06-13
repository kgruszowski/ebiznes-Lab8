import React, {Component} from "react";
import {Redirect} from "react-router-dom";
import InputComponent from "../Common/InputComponent";

class DiscountAdd extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: 0,
            code: "",
            value: "",
            discountCreated: false
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
    }

    handleSubmit(event) {
        var data = {
            "id": this.state.id,
            "code": this.state.code,
            "value": parseInt(this.state.value)
        };
        var url = 'http://localhost:9000/api/discount';

        fetch(url, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        }).then(results => {
            return results.json();
        }).then(responseData => {
            this.setState({
                discountCreated: true,
                id: responseData.discount.id,
                code: responseData.discount.code,
                value: responseData.discount.value,
            })
        });
    }

    handleInputChange(name, value) {
        this.setState({
            [name]: value
        });
    }

    render() {
        if (this.state.discountCreated) {
            return <Redirect to={"/discount/" + this.state.id}/>
        }
        return (
            <div>
                <InputComponent label="Code" name="code" value={this.state.code}
                                onChangeCallback={this.handleInputChange}/>
                <InputComponent label="Value" name="value" value={this.state.value}
                                onChangeCallback={this.handleInputChange}/>

                <button className="btn btn-sm btn-success" onClick={this.handleSubmit}>Add discount</button>
            </div>
        );
    }
}

export default DiscountAdd;