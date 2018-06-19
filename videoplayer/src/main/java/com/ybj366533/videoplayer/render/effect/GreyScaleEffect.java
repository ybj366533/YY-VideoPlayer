package com.ybj366533.videoplayer.render.effect;

import android.opengl.GLSurfaceView;

import com.ybj366533.videoplayer.render.view.MiguVideoGLView.ShaderInterface;


/**
 * Converts video to GreyScale.
 *
 */
public class GreyScaleEffect implements ShaderInterface {
    /**
     * Initialize Effect
     */
    public GreyScaleEffect() {
    }

    @Override
    public String getShader(GLSurfaceView mGlSurfaceView) {

        String shader = "#extension GL_OES_EGL_image_external : require\n"
                + "precision mediump float;\n"
                + "uniform samplerExternalOES sTexture;\n"
                + "varying vec2 vTextureCoord;\n" + "void main() {\n"
                + "  vec4 color = texture2D(sTexture, vTextureCoord);\n"
                + "  float y = dot(color, vec4(0.299, 0.587, 0.114, 0));\n"
                + "  gl_FragColor = vec4(y, y, y, color.a);\n" + "}\n";
        ;

        return shader;

    }
}
