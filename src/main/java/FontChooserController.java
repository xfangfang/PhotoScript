import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class FontChooserController implements Initializable{
    @FXML
    private Label font;
    @FXML
    private Label type;
    @FXML
    private Label size;
    @FXML
    private ListView<Text> fontList;
    @FXML
    private ListView<Text> typeList;
    @FXML
    private ListView<String> sizeList;
    @FXML
    private ComboBox<HBox> colorList;
    @FXML
    private Button ok;
    @FXML
    private Button cancel;
    @FXML
    private Label sample;
    @FXML
    private TextField input_text;
    // 颜色映射列表
    private Map<String, Color> colorMap;
    // 字号映射列表
    private Map<String, Integer> sizeMap;
    // 默认字形(非加粗)
    private FontWeight weiStyle = FontWeight.NORMAL;
    // 默认字形(非斜体)
    private FontPosture posStyle = FontPosture.REGULAR;
    // 获取到的字体对象
    private SelectedFont selectedFont = null;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // 获取系统字体列表
        List<String> font_families = Font.getFamilies();

        List<Text> textList = new ArrayList<Text>(font_families.size());

        for (int i = 0; i < font_families.size(); i++)
        {
            // 将fontNames.length个Text对象添加到textList中
            textList.add(new Text(font_families.get(i)));
            // 设置字体列表字体
            textList.get(i).setFont(Font.font(font_families.get(i)));
        }


        // 将获得的字体列表放入字体ListView中
        fontList.setItems(FXCollections.observableArrayList(textList));
        // 设置默认选中的字体
        fontList.getSelectionModel().selectFirst();
        // 显示选中的字体名称
        font.setText(fontList.getSelectionModel().getSelectedItem().getText());

        // 字体ListView监听事件
        fontList.getSelectionModel()
                .selectedItemProperty()
                .addListener(event ->
                {
                    // 显示选中的字体名称
                    font.setText(fontList.getSelectionModel().getSelectedItem().getText());
                    // 更新新字体预览效果
                    sample.setFont(Font.font(fontList.getSelectionModel().getSelectedItem().getText(), weiStyle,
                            posStyle,
                            sizeMap.get(sizeList.getSelectionModel().getSelectedItem())));
                });

        // 实例化字形列表内容
        Text normal_regular = new Text("常规");
        Text bold_regular = new Text("加粗");
        Text normal_italic = new Text("斜体");
        Text bold_italic = new Text("粗斜体");
        // 设置字形列表字体
        normal_regular.setFont(Font.font(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, Font
                .getDefault().getSize()));
        bold_regular.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, FontPosture.REGULAR, Font
                .getDefault().getSize()));
        normal_italic.setFont(Font.font(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.ITALIC, Font
                .getDefault().getSize()));
        bold_italic.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, FontPosture.ITALIC, Font
                .getDefault().getSize()));
        // 将字体类型名称放入字形ListView中
        typeList.setItems(FXCollections.observableArrayList(normal_regular, bold_regular, normal_italic, bold_italic));
        // 设置默认选中的字称
        typeList.getSelectionModel().selectFirst();
        // 显示选中的字形名称
        type.setText(typeList.getSelectionModel().getSelectedItem().getText());
        // 字形ListView监听事件
        typeList.getSelectionModel()
                .selectedItemProperty()
                .addListener(event ->
                {
                    String value = typeList.getSelectionModel().getSelectedItem().getText();
                    if (value.equals("常规"))
                    {
                        weiStyle = FontWeight.NORMAL;
                        posStyle = FontPosture.REGULAR;
                    }
                    else if (value.equals("加粗"))
                    {
                        weiStyle = FontWeight.BOLD;
                        posStyle = FontPosture.REGULAR;
                    }
                    else if (value.equals("斜体"))
                    {
                        weiStyle = FontWeight.NORMAL;
                        posStyle = FontPosture.ITALIC;
                    }
                    else if (value.equals("粗斜体"))
                    {
                        weiStyle = FontWeight.BOLD;
                        posStyle = FontPosture.ITALIC;
                    }
                    type.setText(value);
                    // 更新预览文字字形效果
                    sample.setFont(Font.font(fontList.getSelectionModel().getSelectedItem().getText(), weiStyle,
                            posStyle,
                            sizeMap.get(sizeList.getSelectionModel().getSelectedItem())));
                });
        // 字号字符串
        String sizeStr[] = { "8", "9", "10", "11", "12", "14", "16", "18", "20", "22",
                "24", "26", "28", "36", "48", "72", "初号", "小初", "一号", "小一", "二号", "小二", "三号", "小三", "四号", "小四", "五号",
                "小五", "六号", "小六", "七号", "八号" };
        // 字号值
        int sizeValue[] = { 8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72, 42, 36, 26, 24, 22, 18, 16,
                15, 14, 12, 11, 9, 8, 7, 6, 5 };
        sizeList.setItems(FXCollections.observableArrayList(sizeStr));

        // 实例化字号映射表
        sizeMap = new HashMap<String, Integer>();
        // 循环得到每个字号字符串对应的值
        for (int i = 0; i < sizeList.getItems().size(); ++i)
        {
            sizeMap.put(sizeStr[i], sizeValue[i]);
        }
        // 设置默认选中字号
        sizeList.getSelectionModel().select(4);
        // 显示选中的字号
        size.setText(sizeList.getSelectionModel().getSelectedItem());
        // 字号ListView监听事件
        sizeList.getSelectionModel().selectedItemProperty().addListener(event ->
        {
            size.setText(sizeList.getSelectionModel().getSelectedItem());
            sample.setFont(Font.font(fontList.getSelectionModel().getSelectedItem().getText(), weiStyle, posStyle,
                    sizeMap.get(sizeList.getSelectionModel().getSelectedItem())));
        });

        // 颜色字符串列表
        String colorStr[] = { "黑色", "蓝色", "青色", "深灰", "灰色", "绿色", "浅灰", "洋红", "桔黄",
                "粉红", "红色", "白色", "黄色" };

        // 颜色值列表
        Color[] colorValue = new Color[] {
                Color.BLACK, Color.BLUE, Color.CYAN, Color.DARKGRAY, Color.GRAY, Color.GREEN, Color.LIGHTGRAY,
                Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED, Color.WHITE, Color.YELLOW
        };

        List<HBox> hboxList = new ArrayList<HBox>(colorValue.length);
        for (int i = 0; i < colorValue.length; i++)
        {
            Rectangle rect = new Rectangle(80, 15);
            rect.setFill(colorValue[i]);
            HBox hBox = new HBox();
            Label label = new Label(colorStr[i]);
            label.setTextFill(colorValue[0]);
            hBox.setSpacing(5);
            hBox.getChildren().addAll(label, rect);
            hboxList.add(hBox);
        }

        // 将颜色字符串放入颜色ChoiceBox中
        colorList.setItems(FXCollections.observableArrayList(hboxList));
        // 设置默认颜色
        colorList.getSelectionModel().select(0);

        // 实例化颜色映射列表
        colorMap = new HashMap<String, Color>();
        // 循环得到每个颜色字符串对应的颜色值
        for (int i = 0; i < colorList.getItems().size(); i++)
        {
            colorMap.put(colorStr[i], colorValue[i]);
        }

        // 颜色ComboBox监听事件
        colorList.getSelectionModel().selectedItemProperty().addListener(event ->
        {
            colorList.getSelectionModel().getSelectedItem().getChildren().forEach(node ->
            {
                if (node.getClass().equals(Label.class))
                {
                    Label label = (Label) node;
                    // 更新预览文字颜色
                    sample.setTextFill(colorMap.get(label.getText()));
                }
            });
        });

        // 示例文字初始化样式
        sample.setFont(Font.font(fontList.getSelectionModel().getSelectedItem().getText(), weiStyle, posStyle,
                sizeMap.get(sizeList.getSelectionModel().getSelectedItem())));

        input_text.setText("Java CS1508-directed by WBT");

    }

    public void setInitText(String text){
        input_text.setText(text);
    }


    public void okClick(ActionEvent event){
        // 将获取到的字体信息保存到字体对象中
        selectedFont = new SelectedFont();
        // 保存字体、字形及字号
        selectedFont.setFont(Font.font(fontList.getSelectionModel().getSelectedItem().getText(), weiStyle,
                posStyle,
                sizeMap.get(sizeList.getSelectionModel().getSelectedItem())));

        colorList.getSelectionModel().getSelectedItem().getChildren().forEach(node ->
        {
            if (node.getClass().equals(Rectangle.class))
            {
                Rectangle rect = (Rectangle) node;
                // 保存颜色
                selectedFont.setColor((Color) rect.getFill());
            }
        });

        selectedFont.setInputString(input_text.getText()); //保存输入的文本
        System.out.println("文本："+selectedFont.getInputString()+" "+"风格："+selectedFont.getFont().getFamily()+" 大小： "+selectedFont.getFont().getSize());

        // 关闭窗口
        ((Stage) ok.getScene().getWindow()).hide();

    }

    public void cancelClick(ActionEvent event){
        ((Stage) cancel.getScene().getWindow()).hide();
    }

    class SelectedFont
    {
        private Font font;
        private Color color;
        private String inputString = "初始是我";

        public Font getFont()
        {
            return font;
        }

        public void setFont(Font font)
        {
            this.font = font;
        }

        public Color getColor()
        {
            return color;
        }

        public void setColor(Color color)
        {
            this.color = color;
        }

        public String getInputString(){
            return this.inputString;
        }
        public void setInputString(String inputString){
            this.inputString = inputString;
        }
    }

    public SelectedFont getSelectedFont(){
        return this.selectedFont;
    }

}

