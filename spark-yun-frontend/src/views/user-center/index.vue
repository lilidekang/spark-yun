<template>
  <Breadcrumb :bread-crumb-list="breadCrumbList" />
  <div class="zqy-seach-table">
    <div class="zqy-table-top">
      <el-button
        type="primary"
        @click="addData"
      >
        添加用户
      </el-button>
      <div class="zqy-seach">
        <el-input
          v-model="keyword"
          placeholder="请输入用户名/手机号/邮箱 回车进行搜索"
          :maxlength="200"
          clearable
          @input="inputEvent"
          @keyup.enter="initData(false)"
        />
      </div>
    </div>
    <LoadingPage
      :visible="loading"
      :network-error="networkError"
      @loading-refresh="initData(false)"
    >
      <div class="zqy-table">
        <BlockTable
          :table-config="tableConfig"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        >
          <template #statusTag="scopeSlot">
            <div class="btn-group">
              <el-tag
                v-if="scopeSlot.row.status === 'ENABLE'"
                class="ml-2"
                type="success"
              >
                启用
              </el-tag>
              <el-tag
                v-if="scopeSlot.row.status === 'DISABLE'"
                class="ml-2"
                type="danger"
              >
                禁用
              </el-tag>
            </div>
          </template>
          <template #options="scopeSlot">
            <div class="btn-group">
              <span @click="editData(scopeSlot.row)">编辑</span>
              <template v-if="scopeSlot.row.status === 'ENABLE'">
                <span
                  v-if="!scopeSlot.row.statusLoading"
                  @click="changeStatus(scopeSlot.row, false)"
                >禁用</span>
                <el-icon
                  v-else
                  class="is-loading"
                >
                  <Loading />
                </el-icon>
              </template>
              <template v-else>
                <span
                  v-if="!scopeSlot.row.statusLoading"
                  @click="changeStatus(scopeSlot.row, true)"
                >启用</span>
                <el-icon
                  v-else
                  class="is-loading"
                >
                  <Loading />
                </el-icon>
              </template>
              <span @click="deleteData(scopeSlot.row)">删除</span>
            </div>
          </template>
        </BlockTable>
      </div>
    </LoadingPage>
    <AddModal ref="addModalRef" />
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import Breadcrumb from '@/layout/bread-crumb/index.vue'
import BlockTable from '@/components/block-table/index.vue'
import LoadingPage from '@/components/loading/index.vue'
import AddModal from './add-modal/index.vue'

import { BreadCrumbList, TableConfig } from './user-center.config'
import { GetUserCenterList, DisableUser, EnableUser, DeleteUser, AddUserData, UpdateUserData } from '@/services/user-center.service'
import { ElMessage, ElMessageBox } from 'element-plus'

interface FormUser {
  account: string;
  email: string;
  passwd?: string;
  phone: string;
  remark: string;
  username: string;
  id?: string;
}

const breadCrumbList = reactive(BreadCrumbList)
const tableConfig: any = reactive(TableConfig)
const keyword = ref('')
const loading = ref(false)
const networkError = ref(false)
const addModalRef = ref(null)

function initData(tableLoading?: boolean) {
  loading.value = tableLoading ? false : true
  networkError.value = networkError.value || false
  GetUserCenterList({
    page: tableConfig.pagination.currentPage - 1,
    pageSize: tableConfig.pagination.pageSize,
    searchKeyWord: keyword.value
  })
    .then((res: any) => {
      tableConfig.tableData = res.data.content
      tableConfig.pagination.total = res.data.totalElements
      loading.value = false
      tableConfig.loading = false
      networkError.value = false
    })
    .catch(() => {
      tableConfig.tableData = []
      tableConfig.pagination.total = 0
      loading.value = false
      tableConfig.loading = false
      networkError.value = true
    })
}

function addData() {
  addModalRef.value.showModal((formData: FormUser) => {
    return new Promise((resolve: any, reject: any) => {
      AddUserData(formData)
        .then((res: any) => {
          ElMessage.success(res.msg)
          initData()
          resolve()
        })
        .catch((error: any) => {
          reject(error)
        })
    })
  })
}

function editData(data: any) {
  addModalRef.value.showModal((formData: FormUser) => {
    return new Promise((resolve: any, reject: any) => {
      UpdateUserData(formData)
        .then((res: any) => {
          ElMessage.success(res.msg)
          initData()
          resolve()
        })
        .catch((error: any) => {
          reject(error)
        })
    })
  }, data)
}

// 启用 or 禁用
function changeStatus(data: any, status: boolean) {
  data.statusLoading = true
  if (status) {
    EnableUser({
      userId: data.id
    })
      .then((res: any) => {
        ElMessage.success(res.msg)
        data.statusLoading = false
        initData(true)
      })
      .catch(() => {
        data.statusLoading = false
      })
  } else {
    DisableUser({
      userId: data.id
    })
      .then((res: any) => {
        ElMessage.success(res.msg)
        data.statusLoading = false
        initData(true)
      })
      .catch(() => {
        data.statusLoading = false
      })
  }
}

// 删除
function deleteData(data: any) {
  ElMessageBox.confirm('确定删除该成员吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    DeleteUser({
      userId: data.id
    })
      .then((res: any) => {
        ElMessage.success(res.msg)
        initData()
      })
      .catch(() => {})
  })
}

function inputEvent(e: string) {
  if (e === '') {
    initData()
  }
}

function handleSizeChange(e: number) {
  tableConfig.pagination.pageSize = e
  initData()
}

function handleCurrentChange(e: number) {
  tableConfig.pagination.currentPage = e
  initData()
}

onMounted(() => {
  tableConfig.pagination.currentPage = 1
  tableConfig.pagination.pageSize = 10
  initData()
})
</script>
