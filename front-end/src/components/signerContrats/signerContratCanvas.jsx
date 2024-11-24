import React from "react";
import { ReactSketchCanvas } from "react-sketch-canvas";

const SignerContratCanvas = ({ canvasRef, setDrewSomething, errorKeySignature, setErrorKeySignature }) => {

  return (
    <div className="w-full">
        <ReactSketchCanvas
            ref={canvasRef}
            strokeWidth={5}
            strokeColor="black"
            onStroke={() => {
                setDrewSomething(true)
                setErrorKeySignature("");
            }}
            style={{border: `0.0625rem solid ${errorKeySignature.length > 0 ? "red" : "rgb(156, 156, 156)"}`}}
        />
    </div>
  );
};

export default SignerContratCanvas;