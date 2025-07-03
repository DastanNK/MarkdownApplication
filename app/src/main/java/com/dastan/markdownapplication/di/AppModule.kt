package com.dastan.markdownapplication.di

import android.content.Context
import androidx.room.Room
import com.dastan.markdownapplication.data.local.AppDatabase
import com.dastan.markdownapplication.data.local.FileDao
import com.dastan.markdownapplication.data.repositiry.CachedFileRepository
import com.dastan.markdownapplication.data.repositiry.EditFileRepository
import com.dastan.markdownapplication.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext ctx: Context
    ): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "markdown3.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideFileDao(db: AppDatabase): FileDao = db.fileDao()

    @Provides
    @Singleton
    fun provideCachedFileRepository(
        dao: FileDao
    ): CachedFileRepository = CachedFileRepository(dao)

    @Provides
    @Singleton
    fun provideEditFileRepository(
        dao: FileDao
    ): EditFileRepository = EditFileRepository(dao)

    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideGetAllFilesUseCase(repo: CachedFileRepository) =
        GetAllFilesUseCase(repo)

    @Provides
    fun provideGetLastOpenedUseCase(repo: CachedFileRepository) =
        GetLastOpenedFileUseCase(repo)

    @Provides
    fun provideMarkFileOpenedUseCase(repo: CachedFileRepository) =
        MarkFileOpenedUseCase(repo)
    @Provides
    fun provideRenameFileUseCase() =
        RenameFileUseCase()

    @Provides
    fun provideImportFileUseCase(
        renameFileUseCase: RenameFileUseCase,
        repo: CachedFileRepository
    ) = ImportFileUseCase(renameFileUseCase, repo)

    @Provides
    fun provideImportFromUriUseCase(
        importFile: ImportFileUseCase,
        ioDispatcher: CoroutineDispatcher
    ) = ImportFromUriUseCase(importFile, ioDispatcher)

    @Provides
    fun provideImportFromUrlUseCase(
        importFile: ImportFileUseCase,
        ioDispatcher: CoroutineDispatcher
    ) = ImportFromUrlUseCase(importFile, ioDispatcher)

    @Provides
    fun provideParseMarkdownUseCase() = ParseMarkdownUseCase()

    @Provides
    fun provideUpdateFileUseCase(repo: EditFileRepository) = UpdateFileUseCase(repo)


}