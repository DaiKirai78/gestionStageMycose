import React from "react";
import { ReactSketchCanvas } from "react-sketch-canvas";

const SignerContratCanvas = ({ canvasRef }) => {

  return (
    <div className="w-full">
        <ReactSketchCanvas
            ref={canvasRef}
            strokeWidth={5}
            strokeColor="black"
        />
    </div>
  );
};

export default SignerContratCanvas;