package main

import (
    "fmt"
    "encoding/json"

    "github.com/hyperledger/fabric-chaincode-go/shim"
    "github.com/hyperledger/fabric-protos-go/peer"
)

type SimpleChaincode struct{}

type Student struct {
    ID      string `json:"id"`
    Name    string `json:"name"`
    Age     int    `json:"age"`
    Major   string `json:"major"`
}

func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) peer.Response {
    return shim.Success(nil)
}

func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
    function, args := stub.GetFunctionAndParameters()

    if function == "createStudent" {
        return t.createStudent(stub, args)
    } else if function == "queryStudent" {
        return t.queryStudent(stub, args)
    } else if function == "updateStudent" {
        return t.updateStudent(stub, args)
    } else if function == "deleteStudent" {
        return t.deleteStudent(stub, args)
    }

    return shim.Error("Invalid function name")
}

func (t *SimpleChaincode) createStudent(stub shim.ChaincodeStubInterface, args []string) peer.Response {
    if len(args) != 4 {
        return shim.Error("Incorrect number of arguments. Expecting 4: id, name, age, major")
    }

    id := args[0]
    name := args[1]

    var age int
    _, err := fmt.Sscanf(args[2], "%d", &age)
    if err != nil {
        return shim.Error("Age must be an integer")
    }

    major := args[3]

    student := Student{
        ID:    id,
        Name:  name,
        Age:   age,
        Major: major,
    }

    studentBytes, err := json.Marshal(student)
    if err != nil {
        return shim.Error(err.Error())
    }

    err = stub.PutState(id, studentBytes)
    if err != nil {
        return shim.Error(err.Error())
    }

    return shim.Success(nil)
}

func (t *SimpleChaincode) queryStudent(stub shim.ChaincodeStubInterface, args []string) peer.Response {
    if len(args) != 1 {
        return shim.Error("Incorrect number of arguments. Expecting 1: id")
    }

    id := args[0]

    studentBytes, err := stub.GetState(id)
    if err != nil {
        return shim.Error(err.Error())
    }
    if studentBytes == nil {
        return shim.Error("Student not found")
    }

    return shim.Success(studentBytes)
}

func (t *SimpleChaincode) updateStudent(stub shim.ChaincodeStubInterface, args []string) peer.Response {
    if len(args) != 4 {
        return shim.Error("Incorrect number of arguments. Expecting 4: id, name, age, major")
    }

    id := args[0]

    studentBytes, err := stub.GetState(id)
    if err != nil {
        return shim.Error(err.Error())
    }
    if studentBytes == nil {
        return shim.Error("Student not found")
    }

    name := args[1]

    var age int
    _, err = fmt.Sscanf(args[2], "%d", &age)
    if err != nil {
        return shim.Error("Age must be an integer")
    }

    major := args[3]

    updatedStudent := Student{
        ID:    id,
        Name:  name,
        Age:   age,
        Major: major,
    }

    updatedBytes, err := json.Marshal(updatedStudent)
    if err != nil {
        return shim.Error(err.Error())
    }

    err = stub.PutState(id, updatedBytes)
    if err != nil {
        return shim.Error(err.Error())
    }

    return shim.Success(nil)
}

func (t *SimpleChaincode) deleteStudent(stub shim.ChaincodeStubInterface, args []string) peer.Response {
    if len(args) != 1 {
        return shim.Error("Incorrect number of arguments. Expecting 1: id")
    }

    id := args[0]

    studentBytes, err := stub.GetState(id)
    if err != nil {
        return shim.Error(err.Error())
    }
    if studentBytes == nil {
        return shim.Error("Student not found")
    }

    err = stub.DelState(id)
    if err != nil {
        return shim.Error(err.Error())
    }

    return shim.Success(nil)
}


func main() {
    err := shim.Start(new(SimpleChaincode))
    if err != nil {
        fmt.Printf("Error starting chaincode: %s", err)
    }
}
