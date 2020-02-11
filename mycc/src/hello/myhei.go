package main

import (
    "fmt"
    "strconv"
    "github.com/hyperledger/fabric/core/chaincode/shim"
    pb "github.com/hyperledger/fabric/protos/peer"
)

type LGSimpleChaincode struct {
}

func (t *LGSimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
    fmt.Println("LGSimpleChaincode -> Init")
    _, args := stub.GetFunctionAndParameters()
    var A, B string
    var Aval, Bval int
    var err error

    if len(args) != 4 {
        return shim.Error("参数数量不对")
    }

    A = args[0]
    Aval, err = strconv.Atoi(args[1])
    if err != nil {
        return shim.Error("参数值类型不对")
    }

    B = args[2]
    Bval, err = strconv.Atoi(args[3])
    if err != nil {
        return shim.Error("参数值类型不对")
    }

    err = stub.PutState(A, []byte(strconv.Itoa(Aval)))
    if err != nil {
        return shim.Error(err.Error())
    }

    err = stub.PutState(B, []byte(strconv.Itoa(Bval)))
    if err != nil {
        return shim.Error(err.Error())
    }

    return shim.Success(nil)
}

func (t *LGSimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
    fmt.Println("LGSimpleChaincode -> Invoke")

    function, args := stub.GetFunctionAndParameters()
    if function == "invoke" {

        return t.invoke(stub, args)
    } else if function == "delete" {

        return t.delete(stub, args)
    } else if function == "query" {

        return t.query(stub, args)
    }

    return shim.Error("方法名不对，只支持 invoke，delete，query")
}

//交易转账
func (t *LGSimpleChaincode) invoke(stub shim.ChaincodeStubInterface, args []string) pb.Response {
    var A, B string
    var Aval, Bval int
    var X int
    var err error

    if len(args) != 3 {
        return shim.Error("参数数量不对")
    }

    A = args[0]
    B = args[1]

    Avalbytes, err := stub.GetState(A)
    if err != nil {
        return shim.Error("没找到第一个的用户金额")
    }
    if Avalbytes == nil {
        return shim.Error("第一个用户金额为nil")
    }
    Aval, _ = strconv.Atoi(string(Avalbytes))

    Bvalbytes, err := stub.GetState(B)
    if err != nil {
        return shim.Error("没找到第二个的用户金额")
    }
    if Bvalbytes == nil {
        return shim.Error("第二个用户金额为nil")
    }
    Bval, _ = strconv.Atoi(string(Bvalbytes))

    X, err = strconv.Atoi(args[2])
    if err != nil {
        return shim.Error("转账金额异常")
    }
    Aval = Aval - X
    Bval = Bval + X
    fmt.Printf("Aval = %d, Bval = %d\n", Aval, Bval)

    //重新更新用户金额
    err = stub.PutState(A, []byte(strconv.Itoa(Aval)))
    if err != nil {
        return shim.Error(err.Error())
    }

    err = stub.PutState(B, []byte(strconv.Itoa(Bval)))
    if err != nil {
        return shim.Error(err.Error())
    }

    return shim.Success(nil)
}

func (t *LGSimpleChaincode) delete(stub shim.ChaincodeStubInterface, args []string) pb.Response {
    if len(args) != 1 {
        return shim.Error("参数异常")
    }
    A := args[0]

    err := stub.DelState(A)
    if err != nil {
        return shim.Error("删除用户异常")
    }

    return shim.Success(nil)
}

func (t *LGSimpleChaincode) query(stub shim.ChaincodeStubInterface, args []string) pb.Response {
    var A string
    var err error

    if len(args) != 1 {
        return shim.Error("查询参数异常")
    }

    A = args[0]

    Avalbytes, err := stub.GetState(A)
    if err != nil {
        jsonResp := "{\"Error\":\"用户金额查询异常 " + A + "\"}"
        return shim.Error(jsonResp)
    }

    if Avalbytes == nil {
        jsonResp := "{\"Error\":\"用户金额为nil " + A + "\"}"
        return shim.Error(jsonResp)
    }

    jsonResp := "{\"Name\":\"" + A + "\",\"Amount\":\"" + string(Avalbytes) + "\"}"
    fmt.Printf("Query Response:%s\n", jsonResp)
    return shim.Success(Avalbytes)
}

func main() {
    err := shim.Start(new(LGSimpleChaincode))
    if err != nil {
        fmt.Printf("链码部署失败: %s", err)
    }
}
