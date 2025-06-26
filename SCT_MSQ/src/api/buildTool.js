import request from '@/common/request'

export function convertTxtToExcel(file) {
  const formData = new FormData();
  formData.append('file', file);
  return request({
    url: '/convert',
    method: 'post',
    data: formData,
    responseType: 'blob',
    headers: { 'Content-Type': undefined }
  })
} 