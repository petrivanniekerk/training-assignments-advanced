package com.jme3.scene;

import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.IntMap;
import com.jme3.util.SafeArrayList;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;

public class MeshCloning extends Mesh implements Cloneable, JmeCloneable {
	
	/**
     * Create a shallow clone of this Mesh. The {@link VertexBuffer vertex
     * buffers} are shared between this and the clone mesh, the rest
     * of the data is cloned.
     *
     * @return A shallow clone of the mesh
     */
    @Override
    public Mesh clone() {
        try {
            Mesh clone = (Mesh) super.clone();
            clone.meshBound = meshBound.clone();
            clone.collisionTree = collisionTree != null ? collisionTree : null;
            clone.buffers = buffers.clone();
            clone.buffersList = new SafeArrayList<VertexBuffer>(VertexBuffer.class,buffersList);
            clone.vertexArrayID = -1;
            if (elementLengths != null) {
                clone.elementLengths = elementLengths.clone();
            }
            if (modeStart != null) {
                clone.modeStart = modeStart.clone();
            }
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    /**
     * Creates a deep clone of this mesh.
     * The {@link VertexBuffer vertex buffers} and the data inside them
     * is cloned.
     *
     * @return a deep clone of this mesh.
     */
    public Mesh deepClone(){
        try{
            Mesh clone = (Mesh) super.clone();
            clone.meshBound = meshBound != null ? meshBound.clone() : null;

            // TODO: Collision tree cloning
            //clone.collisionTree = collisionTree != null ? collisionTree : null;
            clone.collisionTree = null; // it will get re-generated in any case

            clone.buffers = new IntMap<VertexBuffer>();
            clone.buffersList = new SafeArrayList<VertexBuffer>(VertexBuffer.class);
            for (VertexBuffer vb : buffersList.getArray()){
                VertexBuffer bufClone = vb.clone();
                clone.buffers.put(vb.getBufferType().ordinal(), bufClone);
                clone.buffersList.add(bufClone);
            }

            clone.vertexArrayID = -1;
            clone.vertCount = vertCount;
            clone.elementCount = elementCount;
            clone.instanceCount = instanceCount;

            // although this could change
            // if the bone weight/index buffers are modified
            clone.maxNumWeights = maxNumWeights;

            clone.elementLengths = elementLengths != null ? elementLengths.clone() : null;
            clone.modeStart = modeStart != null ? modeStart.clone() : null;
            return clone;
        }catch (CloneNotSupportedException ex){
            throw new AssertionError();
        }
    }

    /**
     * Clone the mesh for animation use.
     * This creates a shallow clone of the mesh, sharing most
     * of the {@link VertexBuffer vertex buffer} data, however the
     * {@link Type#Position}, {@link Type#Normal}, and {@link Type#Tangent} buffers
     * are deeply cloned.
     *
     * @return A clone of the mesh for animation use.
     */
    public Mesh cloneForAnim(){
        Mesh clone = clone();
        if (getBuffer(Type.BindPosePosition) != null){
            VertexBuffer oldPos = getBuffer(Type.Position);

            // NOTE: creates deep clone
            VertexBuffer newPos = oldPos.clone();
            clone.clearBuffer(Type.Position);
            clone.setBuffer(newPos);

            if (getBuffer(Type.BindPoseNormal) != null){
                VertexBuffer oldNorm = getBuffer(Type.Normal);
                VertexBuffer newNorm = oldNorm.clone();
                clone.clearBuffer(Type.Normal);
                clone.setBuffer(newNorm);

                if (getBuffer(Type.BindPoseTangent) != null){
                    VertexBuffer oldTang = getBuffer(Type.Tangent);
                    VertexBuffer newTang = oldTang.clone();
                    clone.clearBuffer(Type.Tangent);
                    clone.setBuffer(newTang);
                }
            }
        }
        return clone;
    }

    /**
     *  Called internally by com.jme3.util.clone.Cloner.  Do not call directly.
     */
    @Override
    public Mesh jmeClone() {
        try {
            Mesh clone = (Mesh)super.clone();
            clone.vertexArrayID = -1;
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    /**
     *  Called internally by com.jme3.util.clone.Cloner.  Do not call directly.
     */
    @Override
    public void cloneFields( Cloner cloner, Object original ) {

        // Probably could clone this now but it will get regenerated anyway.
        this.collisionTree = null;

        this.meshBound = cloner.clone(meshBound);
        this.buffersList = cloner.clone(buffersList);
        this.buffers = cloner.clone(buffers);
        this.lodLevels = cloner.clone(lodLevels);
        this.elementLengths = cloner.clone(elementLengths);
        this.modeStart = cloner.clone(modeStart);
    }

}
