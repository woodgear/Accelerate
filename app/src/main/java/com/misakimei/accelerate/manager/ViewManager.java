package com.misakimei.accelerate.manager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.misakimei.accelerate.Camera;
import com.misakimei.accelerate.FileNameView;
import com.misakimei.accelerate.OptionView;
import com.misakimei.accelerate.R;
import com.misakimei.accelerate.tool.L;
import com.misakimei.accelerate.view.DrawView;
import com.misakimei.accelerate.view.LoadView;
import com.misakimei.accelerate.view.SaveView;
import com.misakimei.accelerate.view.SettingView;
import com.misakimei.accelerate.view.brush_width_view;
import com.misakimei.accelerate.view.colorpick.ColorPickView;
import com.misakimei.accelerate.view.colorpick.ColorPicker;


/**
 * Created by 吴聪 on 2016/5/9.
 * TODO 需要大改 代码不简洁 mvp的意思大概是给每个控件一个接口 约定其功能
 */
public class ViewManager {
    private static final String TAG = "ViewManager";
    private static ViewManager manager;
    brush_width_view brushView;
    ColorPickView colorPickView;
    OptionView optionView;
    SettingView setView;
    LoadView loadView;
    View fullscreen;
    View drawtoolsContain;
    View brush;
    View color;
    View undo;
    View redo;
    View set;
    View play;
    View edit;
    View option;
    View delete;
    View share;
    LinearLayout drawtools;
    View dotools;
    FrameLayout drawtool;
    FrameLayout watchtool;
    boolean show = true;
    int mcolor = Color.BLACK;
    @NonNull
    VIEWSTATE state = VIEWSTATE.NULL;
    DRAWMODE currentMode;
    private Context mcontext;
    private View contentView;
    private DrawView vdview;
    private Dialog mDialog;
    private Dialog loadDialog;
    private boolean connect = false;

    public static ViewManager getInstance() {
        if (manager == null) {
            manager = new ViewManager();
        }
        return manager;
    }

    public DrawView getDrawView() {
        return vdview;
    }

    public void setContentView(@NonNull View contentView, @NonNull Context context) {
        this.contentView = contentView;
        vdview = (DrawView) contentView.findViewById(R.id.drawView);
        fullscreen = contentView.findViewById(R.id.fullscreen);
        brush = contentView.findViewById(R.id.brush);
        color = contentView.findViewById(R.id.color);
        undo = contentView.findViewById(R.id.undo);
        redo = contentView.findViewById(R.id.redo);
        set = contentView.findViewById(R.id.setting);
        play = contentView.findViewById(R.id.play);
        edit = contentView.findViewById(R.id.edit);
        option = contentView.findViewById(R.id.option);
        delete = contentView.findViewById(R.id.delete);

        drawtools = (LinearLayout) contentView.findViewById(R.id.tools);
        dotools = contentView.findViewById(R.id.dotools);
        drawtool = (FrameLayout) contentView.findViewById(R.id.drawtool);
        watchtool = (FrameLayout) contentView.findViewById(R.id.watchtool);
        drawtoolsContain = contentView.findViewById(R.id.tools);
        share = contentView.findViewById(R.id.share);
        mcontext = context;
        initView(context);
    }

    private void initView(@NonNull final Context context) {

        mcontext = context;
        initDialog();

        //TODO 切换图片应该有更好的方法的 类似 togglebutton的感觉
        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.d(TAG, "触发全屏");
                if (show) {
                    drawtools.setVisibility(View.INVISIBLE);
                    dotools.setVisibility(View.INVISIBLE);
                    fullscreen.setBackgroundResource(R.drawable.ic_fullscreen_exit_black_48dp);
                } else {
                    drawtools.setVisibility(View.VISIBLE);
                    dotools.setVisibility(View.VISIBLE);
                    fullscreen.setBackgroundResource(R.drawable.ic_fullscreen_black_48dp);
                }
                show = !show;

            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommandManager.getInstance().undo();
            }
        });

        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommandManager.getInstance().redo();
            }
        });
        brushView = new brush_width_view(context);

        brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float width = PaintManager.getInstance().getPaintStyle().getSize();
                float ratio = Camera.getInstance().getRatio();
                float paintsize = width / ratio;
                int alpha = PaintManager.getInstance().getPaintStyle().getAlpha();
                L.d(TAG, PaintManager.getInstance().getPaintStyle().toString());
                L.d(TAG, "paint size  " + paintsize + "  width " + width + "  ratio  " + ratio + "  alpha " + alpha);
                brushView.setWidthSlide(paintsize);//将笔画的大小映射到0-100的滑动条上 最小值为10 最大值为410
                brushView.setAlphaSlide(alpha);
                brushView.setPointSize(paintsize);//设置笔画大小的提示
                brushView.setPointColor(PaintManager.getInstance().getPaintStyle().getColor());
                brushView.setPointAlpha(alpha);
                brushView.postInvalidate();

                mDialog.setContentView(brushView);
                mDialog.show();
                state = VIEWSTATE.BRUSH;

            }
        });

        //TODO 进度条和点大小应该在思考一下
        brushView.setBrushChangeListener(new brush_width_view.BrushChangeListener() {
            @Override
            public void OnStrokeWidthChange(float val) {
                L.d(TAG, "PaintChange  " + val);
                brushView.setPointSize(val);
            }

            @Override
            public void OnStrokeAlphaChange(int val) {
                brushView.setPointAlpha(val);
            }
        });


        colorPickView = new ColorPickView(context);
        colorPickView.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                mcolor = color;
            }
        });

        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.setContentView(colorPickView);
                mDialog.show();
                state = VIEWSTATE.COLOR;
            }
        });


        setView = new SettingView(context);

        setView.setCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                L.d(TAG, isChecked ? "开启识别模式" : "关闭识别模式");
                //识别模式的开闭
                CommandManager.getInstance().setShapeDetectMode(isChecked);
            }
        });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.setContentView(setView);
                mDialog.show();
            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mcontext, "时光机", Toast.LENGTH_SHORT).show();
                CommandManager.getInstance().timeMachine();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommandManager.getInstance().setMode(DRAWMODE.DRAW);
            }
        });


        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.setContentView(optionView);
                mDialog.show();
            }
        });


        optionView = new OptionView(context);


        //TODO 这里我感觉有很多问题啊
        optionView.SetOnOptionViewItemClickListener(new OptionView.OnOptionViewItemClickListener() {
            @Override
            public void onCreateClick(View view) {
                L.d(TAG, "新建");
                //想要新建
                final String filename = CommandManager.getInstance().getFileName();
                boolean isSave = CommandManager.getInstance().isSave();
                if (filename != null && isSave) {
                    //fvsv
                    L.d(TAG, "fvsv");
                    CommandManager.getInstance().jumptoDraw("DRAW");

                } else if (filename != null && (!isSave)) {
                    //fvsx
                    //先询问是否保存
                    //是 保存后跳转
                    //否 直接跳转
                    L.d(TAG, "fvsx");
                    SaveView saveView = new SaveView(mcontext);//TODO 修正
                    mDialog.setContentView(saveView);
                    saveView.setListener(new SaveView.SaveListener() {
                        @Override
                        public void OnConfirm() {
                            L.d(TAG, "fvsx 保存");
                            mDialog.dismiss();
                            showLoad(true);
                            loadView.setText("保存中");
                            CommandManager.getInstance().save(filename, new AfterListener() {
                                @Override
                                public void after() {
                                    L.d(TAG, "保存完成");
                                    showLoad(false);
                                    CommandManager.getInstance().jumptoDraw("DRAW");

                                }
                            });
                        }

                        @Override
                        public void Cancle() {
                            mDialog.dismiss();
                            CommandManager.getInstance().jumptoDraw("DRAW");
                        }
                    });
                    mDialog.show();
                } else if (filename == null) {
                    L.d(TAG, "fxsx");
                    //问是否保存
                    //是 =>名字 =>保存 =>跳转
                    //否 =>跳转
                    SaveView saveView = new SaveView(mcontext);//TODO 修正
                    mDialog.setContentView(saveView);
                    saveView.setListener(new SaveView.SaveListener() {
                        @Override
                        public void OnConfirm() {
                            FileNameView fileNameView = new FileNameView(mcontext);
                            mDialog.setContentView(fileNameView);
                            fileNameView.setListener(new FileNameView.OnFileNameGetedListener() {
                                @Override
                                public void onDecide(String filename) {
                                    showLoad(true);
                                    loadView.setText("保存中");
                                    mDialog.dismiss();
                                    CommandManager.getInstance().save(filename, new AfterListener() {
                                        @Override
                                        public void after() {
                                            L.d(TAG, "保存完成");
                                            showLoad(false);
                                            CommandManager.getInstance().jumptoDraw("DRAW");

                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void Cancle() {
                            mDialog.dismiss();
                            CommandManager.getInstance().jumptoDraw("DRAW");
                        }
                    });
                }

            }

            @Override
            public void onShareClick(View view) {
                CommandManager.getInstance().share();
            }

            @Override
            public void onExploerClick(View view) {
                L.d(TAG, "浏览");
                //想要新建
                final String filename = CommandManager.getInstance().getFileName();
                boolean isSave = CommandManager.getInstance().isSave();
                if (filename != null && isSave) {
                    //fvsv
                    L.d(TAG, "fvsv");
                    mDialog.dismiss();
                    CommandManager.getInstance().jumptoExploer("DRAW");

                } else if (filename != null && (!isSave)) {
                    //fvsx
                    //先询问是否保存
                    //是 保存后跳转
                    //否 直接跳转
                    L.d(TAG, "fvsx");
                    SaveView saveView = new SaveView(mcontext);//TODO 修正
                    mDialog.setContentView(saveView);
                    saveView.setListener(new SaveView.SaveListener() {
                        @Override
                        public void OnConfirm() {
                            L.d(TAG, "fvsx 保存");
                            mDialog.dismiss();
                            showLoad(true);
                            loadView.setText("保存中");
                            CommandManager.getInstance().save(filename, new AfterListener() {
                                @Override
                                public void after() {
                                    L.d(TAG, "保存完成");
                                    showLoad(false);
                                    CommandManager.getInstance().jumptoExploer("DRAW");
                                }
                            });
                        }

                        @Override
                        public void Cancle() {
                            mDialog.dismiss();
                            CommandManager.getInstance().jumptoExploer("DRAW");
                        }
                    });
                    mDialog.show();
                } else if (filename == null) {
                    L.d(TAG, "fxsx");
                    //问是否保存
                    //是 =>名字 =>保存 =>跳转
                    //否 =>跳转
                    SaveView saveView = new SaveView(mcontext);//TODO 修正
                    mDialog.setContentView(saveView);
                    saveView.setListener(new SaveView.SaveListener() {
                        @Override
                        public void OnConfirm() {
                            FileNameView fileNameView = new FileNameView(mcontext);
                            mDialog.setContentView(fileNameView);
                            fileNameView.setListener(new FileNameView.OnFileNameGetedListener() {
                                @Override
                                public void onDecide(String filename) {
                                    showLoad(true);
                                    loadView.setText("保存中");
                                    CommandManager.getInstance().save(filename, new AfterListener() {
                                        @Override
                                        public void after() {
                                            L.d(TAG, "保存完成");
                                            CommandManager.getInstance().jumptoExploer("DRAW");
                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void Cancle() {
                            mDialog.dismiss();
                            CommandManager.getInstance().jumptoExploer("DRAW");
                        }
                    });
                }
            }

            @Override
            public void onSaveClick(View view) {
                L.d(TAG, "保存");
                //现在已经弹出了选项框

                //想要保存
                final String filename = CommandManager.getInstance().getFileName();
                boolean isSave = CommandManager.getInstance().isSave();
                if (filename != null && isSave) {
                    //fvsv
                    L.d(TAG, "fvsv");
                    mDialog.dismiss();
                    showToast("文件已保存");

                } else if (filename != null && (!isSave)) {
                    //fvsx
                    //直接保存
                    L.d(TAG, "fvsx");
                    mDialog.dismiss();
                    showLoad(true);
                    loadView.setText("保存中");
                    CommandManager.getInstance().save(filename, new AfterListener() {
                        @Override
                        public void after() {
                            L.d(TAG, "保存完成");
                            CommandManager.getInstance().setSave(true);
                            showLoad(false);
                        }
                    });

                } else if (filename == null) {
                    L.d(TAG, "fxsx");
                    //问是否保存
                    //是 =>名字 =>保存 =>跳转
                    //否 =>跳转
                    FileNameView fileNameView = new FileNameView(mcontext);
                    mDialog.dismiss();//取消选项
                    L.d(TAG, "取消选项");

                    mDialog.setContentView(fileNameView);
                    mDialog.show();
                    fileNameView.setListener(new FileNameView.OnFileNameGetedListener() {
                        @Override
                        public void onDecide(final String filename) {
                            mDialog.dismiss();//取消问名
                            L.d(TAG, "取消问名");
                            showLoad(true);
                            loadView.setText("保存中");
                            CommandManager.getInstance().save(filename, new AfterListener() {
                                @Override
                                public void after() {
                                    showLoad(false);
                                    CommandManager.getInstance().setSave(true);
                                    CommandManager.getInstance().setFileName(filename);
                                }
                            });
                        }
                    });
                }
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                L.d(TAG, "mDialog dismiss  " + state.name());
                switch (state) {
                    case BRUSH:
                        int color = brushView.getStrokeColor();
                        int alpha = brushView.getStrokeAlpha();
                        float size = brushView.getStrokeSize();
                        size *= Camera.getInstance().getRatio();
                        CommandManager.getInstance().paintColorChange(color, alpha, size);
                        L.d(TAG, "paint change  color " + color + " size " + size);
                        L.d(TAG, "paint change  " + PaintManager.getInstance().getPaintStyle().toString());
                        break;

                    case COLOR:
                        int al = PaintManager.getInstance().getPaintStyle().getAlpha();
                        float si = PaintManager.getInstance().getPaintStyle().getSize();
                        L.d(TAG, "调色盘 透明度" + al);
                        CommandManager.getInstance().paintColorChange(mcolor, al, si);
                        break;
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadView.setText("删除中---");
                showLoad(true);
                CommandManager.getInstance().delete(CommandManager.getInstance().getFileName(), new AfterListener() {
                    @Override
                    public void after() {
                        showLoad(false);
                        CommandManager.getInstance().jumptoExploer("DRAW");
                    }
                });
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommandManager.getInstance().share();
            }
        });


    }

    private void initDialog() {

        mDialog = new Dialog(mcontext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        loadDialog = new Dialog(mcontext);
        loadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadView = new LoadView(mcontext);
        loadDialog.setContentView(loadView);
        loadDialog.setCancelable(false);
        loadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    public void changeMode(@NonNull DRAWMODE mode) {
        if (currentMode == null || currentMode != mode) {
            currentMode = mode;
        }
        setVisible(mode);

    }

    private void setVisible(@NonNull DRAWMODE mode) {
        L.d(TAG, "切换成工具栏 " + mode.name());
        if (mode == DRAWMODE.DRAW) {
            L.d(TAG, "显示draw 工具 隐藏 watch 工具");
            watchtool.setVisibility(View.INVISIBLE);

            drawtool.setVisibility(View.VISIBLE);
        } else {
            watchtool.setVisibility(View.VISIBLE);
            drawtool.setVisibility(View.INVISIBLE);
        }
    }

    private void showToast(String text) {
        Toast.makeText(mcontext, text, Toast.LENGTH_SHORT).show();
    }

    private void showLoad(boolean isShow) {
        loadDialog.dismiss();
        if (isShow) {
            loadDialog.show();
        } else {
            loadDialog.dismiss();
        }
    }

    enum VIEWSTATE {
        BRUSH, COLOR, OPTION, NULL
    }
}