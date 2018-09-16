package com.jme3.scene;

import java.io.IOException;

import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.bih.BIHTree;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.VertexBuffer.Usage;
import com.jme3.util.IntMap;
import com.jme3.util.IntMap.Entry;

public class MeshIO extends Mesh implements Savable {
	
	public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);

//        HashMap<String, VertexBuffer> map = new HashMap<String, VertexBuffer>();
//        for (Entry<VertexBuffer> buf : buffers){
//            if (buf.getValue() != null)
//                map.put(buf.getKey()+"a", buf.getValue());
//        }
//        out.writeStringSavableMap(map, "buffers", null);

        out.write(meshBound, "modelBound", null);
        out.write(vertCount, "vertCount", -1);
        out.write(elementCount, "elementCount", -1);
        out.write(instanceCount, "instanceCount", -1);
        out.write(maxNumWeights, "max_num_weights", -1);
        out.write(mode, "mode", Mode.Triangles);
        out.write(collisionTree, "collisionTree", null);
        out.write(elementLengths, "elementLengths", null);
        out.write(modeStart, "modeStart", null);
        out.write(pointSize, "pointSize", 1f);

        //Removing HW skinning buffers to not save them
        VertexBuffer hwBoneIndex = null;
        VertexBuffer hwBoneWeight = null;
        hwBoneIndex = getBuffer(Type.HWBoneIndex);
        if (hwBoneIndex != null) {
            buffers.remove(Type.HWBoneIndex.ordinal());
        }
        hwBoneWeight = getBuffer(Type.HWBoneWeight);
        if (hwBoneWeight != null) {
            buffers.remove(Type.HWBoneWeight.ordinal());
        }

        out.writeIntSavableMap(buffers, "buffers", null);

        //restoring Hw skinning buffers.
        if (hwBoneIndex != null) {
            buffers.put(hwBoneIndex.getBufferType().ordinal(), hwBoneIndex);
        }
        if (hwBoneWeight != null) {
            buffers.put(hwBoneWeight.getBufferType().ordinal(), hwBoneWeight);
        }

        out.write(lodLevels, "lodLevels", null);
    }

    @SuppressWarnings("unchecked")
	public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        meshBound = (BoundingVolume) in.readSavable("modelBound", null);
        vertCount = in.readInt("vertCount", -1);
        elementCount = in.readInt("elementCount", -1);
        instanceCount = in.readInt("instanceCount", -1);
        maxNumWeights = in.readInt("max_num_weights", -1);
        mode = in.readEnum("mode", Mode.class, Mode.Triangles);
        elementLengths = in.readIntArray("elementLengths", null);
        modeStart = in.readIntArray("modeStart", null);
        collisionTree = (BIHTree) in.readSavable("collisionTree", null);
        elementLengths = in.readIntArray("elementLengths", null);
        modeStart = in.readIntArray("modeStart", null);
        pointSize = in.readFloat("pointSize", 1f);

//        in.readStringSavableMap("buffers", null);
        buffers = (IntMap<VertexBuffer>) in.readIntSavableMap("buffers", null);
        for (Entry<VertexBuffer> entry : buffers){
            buffersList.add(entry.getValue());
        }

        //creating hw animation buffers empty so that they are put in the cache
        if(isAnimated()){
            VertexBuffer hwBoneIndex = new VertexBuffer(Type.HWBoneIndex);
            hwBoneIndex.setUsage(Usage.CpuOnly);
            setBuffer(hwBoneIndex);
            VertexBuffer hwBoneWeight = new VertexBuffer(Type.HWBoneWeight);
            hwBoneWeight.setUsage(Usage.CpuOnly);
            setBuffer(hwBoneWeight);
        }

        Savable[] lodLevelsSavable = in.readSavableArray("lodLevels", null);
        if (lodLevelsSavable != null) {
            lodLevels = new VertexBuffer[lodLevelsSavable.length];
            System.arraycopy( lodLevelsSavable, 0, lodLevels, 0, lodLevels.length);
        }
    }

}
