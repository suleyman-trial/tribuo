/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tribuo.onnx;

import ai.onnx.proto.OnnxMl;
import com.google.protobuf.ByteString;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Helper functions for building ONNX protos.
 */
public abstract class ONNXUtils {

    /**
     * Private constructor for abstract util class.
     */
    private ONNXUtils() {}

    /**
     * Builds a type proto for the specified shape and tensor type.
     * @param shape The shape.
     * @param type The tensor type.
     * @return The type proto.
     */
    public static OnnxMl.TypeProto buildTensorTypeNode(ONNXShape shape, OnnxMl.TensorProto.DataType type) {
        OnnxMl.TypeProto.Builder builder = OnnxMl.TypeProto.newBuilder();

        OnnxMl.TypeProto.Tensor.Builder tensorBuilder = OnnxMl.TypeProto.Tensor.newBuilder();
        tensorBuilder.setElemType(type.getNumber());
        tensorBuilder.setShape(shape.getProto());
        builder.setTensorType(tensorBuilder.build());

        return builder.build();
    }

    /**
     * Builds a TensorProto containing the array.
     * <p>
     * Downcasts the doubles into floats as ONNX's fp64 support is poor compared to fp32.
     * @param context The naming context.
     * @param name The base name for the proto.
     * @param parameters The array to store in the proto.
     * @return A TensorProto containing the array as floats.
     */
    public static OnnxMl.TensorProto arrayBuilder(ONNXContext context, String name, double[] parameters) {
        OnnxMl.TensorProto.Builder arrBuilder = OnnxMl.TensorProto.newBuilder();
        arrBuilder.setName(context.generateUniqueName(name));
        arrBuilder.addDims(parameters.length);
        arrBuilder.setDataType(OnnxMl.TensorProto.DataType.FLOAT.getNumber());
        ByteBuffer buffer = ByteBuffer.allocate(parameters.length*4).order(ByteOrder.LITTLE_ENDIAN);
        FloatBuffer floatBuffer = buffer.asFloatBuffer();
        for (int i = 0; i < parameters.length; i++) {
            floatBuffer.put((float)parameters.length);
        }
        floatBuffer.rewind();
        arrBuilder.setRawData(ByteString.copyFrom(buffer));
        return arrBuilder.build();
    }

}
