<template>
  <div class="build-tool-container">
    <h2>备货列表生成工具</h2>
    <div class="desp">
        <p>请上传txt文件</p>
        <p>找到你的原理图，点击下面的材料列表，最上面的菜单有一个写入文件</p>
        <p>直接点击保存就行，默认会保存在你的".../Minecraft版本文件夹/config/litematica"下</p>
        <p>如果不知道是哪个可以按时间排序找到最新的</p>
        <p>如果上传之后没有预览窗口出现说明格式不对，需要txt用|---+做成的"表"才能够解析</p>
    </div>
    <el-upload
      class="upload-demo"
      drag
      action=""
      :auto-upload="false"
      :before-upload="handleBeforeUpload"
      :on-change="handleFileChange"
      :file-list="fileList"
      accept=".txt"
      :disabled="loading"
    >
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">将txt文件拖到此处，或<em>点击上传</em></div>
    </el-upload>
    <el-button type="primary" :disabled="!file || loading" @click="handleGenerateExcel" :loading="loading" style="margin-top: 20px;">生成Excel</el-button>
    <div v-if="previewTableData.length" class="preview-window">
      <h3>文件预览：</h3>
      <el-table :data="previewTableData" style="width: 100%">
        <el-table-column v-for="col in previewTableColumns" :key="col.prop" :prop="col.prop" :label="col.label" align="center" />
      </el-table>
    </div>
    <el-loading :loading="loading" text="正在生成Excel，请稍候...">
    </el-loading>
  </div>
</template>

<script>
import { convertTxtToExcel } from '@/api/buildTool'

export default {
  name: 'BuildTool',
  data() {
    return {
      file: null,
      fileList: [],
      previewContent: '',
      previewTableData: [],
      previewTableColumns: [
        { prop: 'name', label: '材料' },
        { prop: 'finished', label: '是否完成' },
        { prop: 'total', label: '总量/个' },
        { prop: 'carton', label: '所需/箱盒' },
        { prop: 'box', label: '所需/盒' },
        { prop: 'group', label: '所需/组' },
        { prop: 'piece', label: '所需/个' },
        { prop: 'owner', label: '负责人' },
        { prop: 'progress', label: '完成进度' },
        { prop: 'location', label: '物品所在位置' }
      ],
      loading: false
    }
  },
  methods: {
    handleBeforeUpload(file) {
      this.file = file;
      return false; // 阻止自动上传
    },
    handleFileChange(fileObj) {
      const file = fileObj.raw;
      this.file = file;
      this.fileList = [fileObj];
      const reader = new FileReader();
      reader.onload = (e) => {
        this.previewContent = e.target.result;
        this.previewTableData = this.parseTxtToTable(this.previewContent);
      };
      reader.readAsText(file, 'utf-8');
    },
    parseTxtToTable(txt) {
      const pattern = /^\|.*\|.*\|.*\|.*\|$/;
      const lines = txt.split(/\r?\n/).map(line => line.trim());
      return lines.filter(line =>
        pattern.test(line) &&
        !line.startsWith('| 材料') &&
        !line.startsWith('| Item')
      ).map(line => {
        const content = line.substring(1, line.length - 1);
        const parts = content.split('|').map(s => s.trim());
        const total = parseInt(parts[1]) || 0;
        const [carton, box, group, piece] = this.calculateUnits(total);
        return {
          name: parts[0] || '',
          finished: '',
          total: total,
          carton,
          box,
          group,
          piece,
          owner: '',
          progress: '',
          location: ''
        }
      });
    },
    calculateUnits(total) {
      const CARTON_TO_BOX = 54;
      const BOX_TO_PIECES = 1728;
      const HALF_BOX = 864;
      const GROUP_TO_PIECES = 64;
      const HALF_GROUP = 32;

      if (total <= 10) {
        return [0, 0, 0, 10];
      }

      const totalBoxesNeeded = Math.ceil(total / BOX_TO_PIECES);

      let cartons = Math.floor(totalBoxesNeeded / CARTON_TO_BOX);
      let remainingBoxes = totalBoxesNeeded % CARTON_TO_BOX;

      let remainingPieces = total % BOX_TO_PIECES;
      let groups = 0;
      let pieces = 0;

      if (remainingPieces > 0) {
        groups = Math.floor(remainingPieces / GROUP_TO_PIECES);
        pieces = remainingPieces % GROUP_TO_PIECES;

        if (pieces >= HALF_GROUP) {
          groups++;
          pieces = 0;
        }

        if (groups * GROUP_TO_PIECES >= HALF_BOX) {
          remainingBoxes++;
          groups = 0;
          pieces = 0;
        }
      }

      return [cartons, remainingBoxes, groups, pieces];
    },
    async handleGenerateExcel() {
      if (!this.file) return;
      this.loading = true;
      try {
        const res = await convertTxtToExcel(this.file);
        const blob = res instanceof Blob ? res : res.data || res;
        // 获取原文件名（去除扩展名）
        let originName = this.file.name || '文件';
        originName = originName.replace(/\.[^.]+$/, '');
        const downloadName = originName + '备货列表.xlsx';
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = downloadName;
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);
      } catch (e) {
        this.$message.error('生成Excel失败');
      } finally {
        this.loading = false;
      }
    }
  }
}
</script>

<style scoped>
body {
  background: #f3f6f9;
}
.build-tool-container {
  max-width: 600px;
  margin: 40px auto;
  padding: 30px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  text-align: center;
}
.preview-window {
  margin-top: 30px;
  text-align: left;
  background: #f7f7f7;
  padding: 16px;
  border-radius: 6px;
  max-height: 300px;
  overflow: auto;
}
.desp p {
    color: red;
}
</style>

<style>
body {
  background: #f3f6f9;
}
</style> 