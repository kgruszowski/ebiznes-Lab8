import React, {Component} from "react";
import {Redirect} from "react-router-dom";
import InputComponent from "../Common/InputComponent";

class DiscountEdit extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: props.match.params.id,
            code: "",
            value: "",
            discountEdited: false
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
        var url = 'http://localhost:9000/api/discount/' + this.state.id;

        fetch(url, {
            method: 'PUT',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        }).then(result => {
            this.setState({productEdited: true})
        });
    }

    handleInputChange(name, value) {
        this.setState({
            [name]: value
        });
    }

    async getdiscount() {
        let url = "http://localhost:9000/api/discount/" + this.state.id;
        fetch(url, {
            mode: 'cors',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': 'http://localhost:3000',
            },
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
            this.setState({
                id: data.discount.id,
                code: data.discount.code,
                value: data.discount.value,
            })
            let inputs = (
                <div>
                    <InputComponent label="Code" name="code" value={this.state.code}
                                    onChangeCallback={this.handleInputChange}/>
                    <InputComponent label="Value" name="value" value={this.state.value}
                                    onChangeCallback={this.handleInputChange}/>
                </div>
            )
            this.setState({inputs: inputs})
        });
    }

    componentDidMount() {
        this.getdiscount();
    }

    render() {
        if (this.state.productEdited) {
            return <Redirect to={"/discount/" + this.state.id}/>
        }
        return (
            <div>
                {this.state.inputs}
                <button className="btn btn-sm btn-success" onClick={this.handleSubmit}>Edit discount</button>
            </div>
        );
    }
}

export default DiscountEdit;